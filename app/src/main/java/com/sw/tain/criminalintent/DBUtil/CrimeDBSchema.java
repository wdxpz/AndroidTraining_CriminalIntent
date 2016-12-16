package com.sw.tain.criminalintent.DBUtil;

/**
 * Created by home on 2016/11/9.
 */

public class CrimeDBSchema {
    public static final class CrimeTable{
        public static final String NAME = "Crime";

        public static final class Col{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT ="suspect";
        }
    }
}
