package com.ncl.sindhu.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String NCL_USER = "ROLE_NCL_USER";

    public static final String NCL_OWNER = "ROLE_NCL_OWNER";

    public static final String NCL_TENANT = "ROLE_NCL_TENANT";

    public static final String NCL_MANAGEMENT = "ROLE_NCL_MANAGEMENT";

    private AuthoritiesConstants() {
    }
}
