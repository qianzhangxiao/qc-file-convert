package com.qzx.convert.utils;

import java.util.Locale;

public class OsUtil {

    public static final String OS_NAME;
    public static final boolean IS_OS_AIX;
    public static final boolean IS_OS_FREE_BSD;
    public static final boolean IS_OS_HP_UX;
    public static final boolean IS_OS_IRIX;
    public static final boolean IS_OS_LINUX;
    public static final boolean IS_OS_MAC;
    public static final boolean IS_OS_MAC_OSX;
    public static final boolean IS_OS_NET_BSD;
    public static final boolean IS_OS_OPEN_BSD;
    public static final boolean IS_OS_SOLARIS;
    public static final boolean IS_OS_SUN_OS;
    public static final boolean IS_OS_UNIX;
    public static final boolean IS_OS_WINDOWS;

    private OsUtil() {
        throw new AssertionError("Utility class must not be instantiated");
    }

    static {
        OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        IS_OS_AIX = OS_NAME.startsWith("aix");
        IS_OS_FREE_BSD = OS_NAME.startsWith("freebsd");
        IS_OS_HP_UX = OS_NAME.startsWith("hp-ux");
        IS_OS_IRIX = OS_NAME.startsWith("irix");
        IS_OS_LINUX = OS_NAME.startsWith("linux");
        IS_OS_MAC = OS_NAME.startsWith("mac");
        IS_OS_MAC_OSX = OS_NAME.startsWith("mac os x");
        IS_OS_NET_BSD = OS_NAME.startsWith("netbsd");
        IS_OS_OPEN_BSD = OS_NAME.startsWith("openbsd");
        IS_OS_SOLARIS = OS_NAME.startsWith("solaris");
        IS_OS_SUN_OS = OS_NAME.startsWith("sunos");
        IS_OS_UNIX = IS_OS_AIX || IS_OS_FREE_BSD || IS_OS_HP_UX || IS_OS_IRIX || IS_OS_LINUX || IS_OS_MAC_OSX || IS_OS_NET_BSD || IS_OS_OPEN_BSD || IS_OS_SOLARIS || IS_OS_SUN_OS;
        IS_OS_WINDOWS = OS_NAME.startsWith("windows");
    }

}
