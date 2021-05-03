package com.agyohora.mobileperitc.data.network;

/**
 * Created by Invent on 3-1-18.
 * constants for API URL's
 */

public class ApiEndPoint {

    public static final String DATA_SERVER_ENDPOINT = "https://admin.elisar.com/api/testresult/postResult";

    //static final String DATA_SERVER_ENDPOINT = "http://ec2-54-175-251-117.compute-1.amazonaws.com/api/testresult/postResult";

    public static final String UPDATE_SERVER_ENDPOINT = "https://s3-ap-southeast-1.amazonaws.com/elisar-updates/latestversiondetails.json";

    public static final String DEV_UPDATE_SERVER_ENDPOINT = "https://s3-ap-southeast-1.amazonaws.com/elisar-updates/latestversiondetailsDev.json";

    public static final String RESTORE_DATABASE_ENDPOINT = "https://ava-restore-db.s3-ap-southeast-1.amazonaws.com/";

}
