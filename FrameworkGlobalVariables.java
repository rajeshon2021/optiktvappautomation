package com.demo.commons.coreframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONObject;

import com.demo.commons.bddinterface.KeywordClassMapperInterface;
import com.demo.commons.deviceCloudProviders.DeviceCloudProviderCredentails;
import com.demo.commons.devicepool.LocalDevicePool;
import com.demo.commons.report.Log4jLogger;
import com.demo.commons.report.PerformanceDatabaseUtils;

public class FrameworkGlobalVariables {

	// ================= Test Suite Related Variables ======================================
	
	public static Properties XRAY_CONFIG = null;
	
	public static int THREAD_COUNT=-1;
	
	public static String SOLUTION_TYPE = "";
	
	public static String SuiteMessage = "No Message provided. Use --message<SPACE>message on command line to provide message.";
	
	public final static int MAX_SCREENSHOTS = 5;
		
	public static String SUITE_CONFIG_PATH = "SuiteConfig.json";
	
	public static boolean SWITCH_TO_XCUITEST = false;
	
	public final static String TESTCASES_PROPERTIES = "TestCases.properties";
	
	public final static String BLANK = "";

	public final static String NORMAL_WIFI = "NORMAL WIFI";
	
	public final static String TESTCASE_NAME = "TESTCASENAME";
	
	public final static String NO_ACCOUNT = "No Account";

	public static JSONObject requirementLookupTable;
	
	public static HashMap<String,DeviceCloudProviderCredentails> dcProviderCredentials = new HashMap<>();
	
	public static String FRAMEWORK_VERSION;
	
	public static PerformanceDatabaseUtils perfDBUtils;

	public static int FALSE_POSITIVE_COUNT = 0; 
	
	public final static String USER_NAME = System.getProperty("user.name")
			.replaceAll("\\s+", BLANK).replaceAll("[\\$:-]+","_");
	
	// ================= Reporting related
	public static boolean DOWNLOAD_VIDEOS_FROM_SAUCELABS = false;

	public static String USER_SPECIFIC_RESULT_FOLDER = BLANK;

	public static String CURRENT_EXECUTION_SUB_FOLDER = BLANK;

	public static boolean INSTANTIATE_DRIVER_GROUP_LEVEL = false;
	
	public static boolean ENABLE_PERFORMANCE_DATA = false;
	
	public static boolean ENABLE_DB_CONNECTION = false;
	
	public static boolean LOG_CREATED = false;

	public static boolean PUBLISH_RESULT = false;
	
	public static boolean PERFORMANCE_RESULTUPLOAD = false;
	
	public static boolean PUBLISH_RESULT_FAILURE_ALERT = false;
	
	public static String TEST_RUN_NAME=BLANK;
	
	public static String REQUIREMENT_FILE_NAME=BLANK;
	
	public static KeywordClassMapperInterface mapper;
	
	public static HashMap<String,String> APP_VERSION_TO_INSTALL= new HashMap<>();
	
	public static HashMap<String,String> APP_Versions = new HashMap<>();

	// ================= Infrastructure related variables
	// ==================================
	public static final String OBJ_DEFINITION_FLD = "src/test/resources/objects_definition/ObjectDefinition";
	public static final String LOCALIZATION_FLD = "src/test/resources/localization";
	public static String LOGS_FLD;
	public static String SCRSHOTS_FLD;
	public static final String RESOURCES_FLD = "src/test/resources/";
	public static final String TSTDATA_FLD = "src/test/resources/test_data/Test_Data/";
	public static final String RESULTS_FOLDER = "src/test/resources/Execution_Folder/";
	public static final String REQUIREMENT_FILES_FOLDER_1 = "src/test/resources/Requirement_Files/";
	public static String CURRENT_FOLDER;
	public static String REBOT_FOLDER;
	public static String REBOT_RETRY_FOLDER;
	public static final String CLIENT_SECRET_FILE = "/GmailAuthFiles/client_secret.json";
	public final static String DEVICE_PROVIDER_FLD = "/Devicecloudprovider/";
//	public final static String SERVERS_FLD = "/ServersAddress/";
	public static final boolean IS_REPORT_SERVER_RUNNING_LOCALLY = true;
	public static final String RESULT_URL = BLANK;
	public final static String POST_TC_FILE = "PostTestCaseSteps.properties";
	
	public final static String MobileDriverFactory = "MobileDriverFactory.properties";
	public final static String DESIREDCAP_FILE = "DesiredCapabilities.properties";
	
	public final static String DRIVER_FILE = "MobileDriverInstantiators.properties";
	

	// securityToken is new replacement of perfecto account password and required for device access
	public final static String securityToken="eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1Y2YzY2NmZC05OTcxLTRiNGUtYTliNi05YmUwZTVmN2Y1MDYifQ.eyJpYXQiOjE2MjY3MDU2MjEsImp0aSI6ImMzMjk1OGE5LWQzOTMtNGU3NC1hYTA2LThjYWM1MjdhNjczZCIsImlzcyI6Imh0dHBzOi8vYXV0aC5wZXJmZWN0b21vYmlsZS5jb20vYXV0aC9yZWFsbXMvaG9uZXl3ZWxsLXBlcmZlY3RvbW9iaWxlLWNvbSIsImF1ZCI6Imh0dHBzOi8vYXV0aC5wZXJmZWN0b21vYmlsZS5jb20vYXV0aC9yZWFsbXMvaG9uZXl3ZWxsLXBlcmZlY3RvbW9iaWxlLWNvbSIsInN1YiI6IjI5NmJjZWZkLWUxYjgtNDZhYi1iMWVlLTVmYTNlNmM0NTk3OSIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJvZmZsaW5lLXRva2VuLWdlbmVyYXRvciIsIm5vbmNlIjoiNTljNDgyNjAtMzkwMS00MDg0LThkMDYtN2EyMGZkMzFkYzdlIiwic2Vzc2lvbl9zdGF0ZSI6IjBmM2FjMjZhLWM3ZTAtNGE1Zi1hNWFjLTZkNzgwZWUwMDhmNSIsInNjb3BlIjoib3BlbmlkIG9mZmxpbmVfYWNjZXNzIn0.2Qf2utZxZvG6WABalEO5n1ueMrcEZlJRMVFg5Kss2Ro";
	
	// virtualdevice
	public final static String useVirtualDevice="true";


	// ================== Capabilities related variables ===========================================
//	public static HashMap<String, Object> suiteProperties = new HashMap<String, Object>();
	public static HashMap<String, Boolean> driverInstanceRequired = new HashMap<String, Boolean>();
	
	public static Log4jLogger logger4J;

	// ================== Object TimeOut related variables
	// ===========================================
	public static final int LONG_WAIT = 15000;
	public static final int POLLING_WAIT = 500;

	// ================== Script related time out
	// ====================================================
	public static final long SCRIPT_TIME_OUT = 2700000;

	// ================== Local Device Pool =========
	public static String SELECTED_GROUP;
	public static final HashMap<TestCaseInputs, LocalDevicePool> LOCAL_POOL = new HashMap<TestCaseInputs, LocalDevicePool>();
	
	// =================== Groups Selected =================
	public static ArrayList<String> GroupSelected = new ArrayList<String>();
	
	public static boolean falsePositive = false;
	
	public static int retryCounter = 0;

}
