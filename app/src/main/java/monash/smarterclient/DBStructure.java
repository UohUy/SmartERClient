package monash.smarterclient;

import android.provider.BaseColumns;

public class DBStructure {
    public static abstract class tableEntry implements BaseColumns{
        public static final String TABLE_NAME = "elecusage";
        public static final String COLUMN_AIRCONDUSAGE = "airCondUsage";
        public static final String COLUMN_FRIDGEUSAGE = "fridgeUsage";
        public static final String COLUMN_RESID = "resid";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_USAGEDATE = "usageDate";
        public static final String COLUMN_USAGEHOUR = "usageHour";
        public static final String COLUMN_USAGEID = "usageid";
        public static final String COLUMN_WASHINGMACHUSAGE = "washingMachUsage";
    }
}
