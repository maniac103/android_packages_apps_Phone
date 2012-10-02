package com.android.phone;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BlackList {
    private static final String LOG_TAG = "BlackList";
    private static final boolean DBG = PhoneApp.DBG_LEVEL >= 2;

    private static final String BLFILE           = "blacklist.dat";
    private static final int BLFILE_VER          = 1;

    private Context mContext;
    private HashSet<PhoneNo> mList = new HashSet<PhoneNo>();

    public BlackList(Context context) {
        mContext = context;
        load();
    }

    private void load() {
        ObjectInputStream ois = null;
        boolean valid = false;

        try {
            ois = new ObjectInputStream(mContext.openFileInput(BLFILE));
            Object o = ois.readObject();
            if (DBG) {
                Log.d(LOG_TAG, "Found object " + o);
            }
            if (o != null) {
                if (o instanceof Integer) {
                    // check the version
                    Integer version = (Integer) o;
                    if (version == BLFILE_VER) {
                        Object numbers = ois.readObject();
                        mList = (HashSet<PhoneNo>) numbers;
                        valid = true;
                    }
                } else {
                    HashSet<String> set = (HashSet<String>) o;
                    mList.clear();
                    for (String s : set) {
                        mList.add(new PhoneNo(s));
                    }
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Opening black list file failed", e);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "Found invalid contents in black list file", e);
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Found invalid contents in black list file", e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
        }

        if (!valid) {
            save();
        }
    }

    private void save() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(mContext.openFileOutput(BLFILE, Context.MODE_PRIVATE));
            oos.writeObject(new Integer(BLFILE_VER));
            oos.writeObject(mList);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not save black list file", e);
            // ignore
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean add(String s) {
        s = stripSeparators(s);
        if (TextUtils.isEmpty(s) || matchesBlacklist(s)) {
            return false;
        }
        mList.add(new PhoneNo(s));
        save();
        return true;
    }

    public void delete(String s) {
        for (PhoneNo number : mList) {
            if (number.equals(s)) {
                mList.remove(number);
                save();
                return;
            }
        }
    }

    public boolean isListed(String s) {
        if (!PhoneSettings.blacklistEnabled(mContext)) {
            return false;
        }
        return matchesBlacklist(s);
    }

    private boolean matchesBlacklist(String s) {
        if (mList.contains(new PhoneNo(s))) {
            return true;
        }
        if (!PhoneSettings.blacklistRegexEnabled(mContext)) {
            return false;
        }

        for (PhoneNo number : mList) {
            // Check for null (technically can't happen)
            // and make sure it doesn't begin with '*' to prevent FC's
            if (number.phone == null || number.phone.startsWith("*")) {
                continue;
            }
            // Escape all +'s. Other regex special chars
            // don't need to be checked for since the phone number
            // is already stripped of separator chars.
            String phone = number.phone.replaceAll("\\+", "\\\\+");
            if (s.matches(phone)) {
                return true;
            }
        }

        return false;
    }

    List<String> getItems() {
        List<String> items = new ArrayList<String>();
        for (PhoneNo number : mList) {
            items.add(number.phone);
        }

        return items;
    }

    /**
     * Custom stripSeparators() method identical to
     * PhoneNumberUtils.stripSeparators(), to retain '.'s
     * for blacklist regex parsing.
     * There is no difference between the two, this is only
     * done to use the custom isNonSeparator() method below.
     */
    private String stripSeparators(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            if (isNonSeparator(c)) {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    /**
     * Custom isNonSeparator() method identical to
     * PhoneNumberUtils.isNonSeparator(), to retain '.'s
     * for blacklist regex parsing.
     * The only difference between the two is that this
     * custom one allows '.'s.
     */
    private boolean isNonSeparator(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+'
                    || c == PhoneNumberUtils.WILD || c == PhoneNumberUtils.WAIT
                    || c == PhoneNumberUtils.PAUSE || c == '.';
    }

    static class PhoneNo implements Comparable<PhoneNo>, java.io.Externalizable, java.io.Serializable {
        static final long serialVersionUID = 32847013274L;

        String phone;

        public PhoneNo() {
            phone = null;
        }

        public PhoneNo(String s) {
            phone = s;
        }

        public int compareTo(PhoneNo bp) {
            if (bp == null || bp.phone == null) {
                return 1;
            }
            if (phone == null) {
                return -1;
            }
            return PhoneNumberUtils.compare(phone, bp.phone) ? 0 : phone.compareTo(bp.phone);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PhoneNo) {
                return compareTo((PhoneNo) o) == 0;
            }
            if (o instanceof CharSequence) {
                return TextUtils.equals((CharSequence) o, phone);
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (phone == null) {
                return 0;
            }
            int len = phone.length();
            return len > 5 ? phone.substring(len - 5).hashCode() : phone.hashCode();
        }

        public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
            out.writeObject(phone);
        }

        public void readExternal(java.io.ObjectInput in) throws java.io.IOException,
                ClassNotFoundException {
            phone = (String) in.readObject();
        }

        public String toString() {
            return "PhoneNo: " + phone;
        }
    }
}
