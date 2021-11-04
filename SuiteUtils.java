package com.demo.commons.coreframework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import com.demo.commons.XLTVUtil.XLTVUtil;
import com.demo.commons.bddinterface.GherkinsUtils;
import com.demo.commons.bddinterface.KeywordClassMapper;
import com.demo.commons.bddinterface.TagsFinder;
import com.demo.commons.coreframework.SuiteConstants.SuiteConstantTypes;
import com.demo.commons.mobile.MobileObject;
import com.demo.commons.report.Log4jLogger;
import com.demo.commons.report.PerformanceDatabaseUtils;
import com.demo.commons.report.Reporting;
import com.demo.commons.report.rebot.RebotReportXML;
import com.demo.commons.report.rebot.ReportUploadUtility;
import com.demo.commons.report.rebot.ReportUploadUtility.ArtifactType;
import com.demo.commons.web.WebObject;

/**
 * SuiteUtils class represents utilities that will help in managing the
 * Framework test suite life cycle.
 * 
 * @author E880579
 * @version 1.0
 */

public class SuiteUtils {

	private static SuiteUtils suite;
	// private String suiteName;
	public String suiteID;
	private List<XmlSuite> xSuites = new ArrayList<XmlSuite>();
	public static String SUITE_RUN_ID;
	public static String SUITE_TYPE="BDD";

	/**
	 * Constructor for class SuiteUtils.
	 * 
	 * @param suiteConstantsPath
	 *            String type variable that represents the suiteConfig files
	 *            path
	 * @param groups
	 *            String[] type variable that represents the groups selected by
	 *            command prompt arguments.
	 * 
	 * @author E880579
	 */

	private SuiteUtils() throws Exception {

		// ======================== Creating Date Log folder
		// ===============================
		if (initializeInfrastructure()) {

			FrameworkGlobalVariables.requirementLookupTable = getRequirementFile();

			if (FrameworkGlobalVariables.requirementLookupTable != null) {

				if (FrameworkGlobalVariables.GroupSelected.size() == 0) {

					Iterator<String> iter = FrameworkGlobalVariables.requirementLookupTable.keys();

					while (iter.hasNext()) {
						FrameworkGlobalVariables.GroupSelected.add(iter.next());
					}
				}

				getTagsLocation(FrameworkGlobalVariables.GroupSelected);

				try {

					if (FrameworkGlobalVariables.TEST_RUN_NAME.isEmpty()) {
						FrameworkGlobalVariables.TEST_RUN_NAME = SuiteConstants
								.getConstantValue(SuiteConstantTypes.TEST_SUITE, "Suite_Name");
					}

					suiteID = SuiteConstants.getConstantValue(SuiteConstantTypes.TEST_SUITE, "Suite_ID");

					// Create TestNG XML file
					if (FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL) {

						xSuites = GherkinsUtils
								.createTestNGSuiteXMLForGroupLevelExecution(FrameworkGlobalVariables.TEST_RUN_NAME);
					} else {
						xSuites.add(GherkinsUtils
								.createTestNGSuiteXMLForNonGroupLevelExecution(FrameworkGlobalVariables.TEST_RUN_NAME));
					}

					if (xSuites.size() == 0) {
						throw new Exception("=================== Error while creating TestNG suite xml file.");
					}

				} catch (Exception e) {
					throw e;
				}

			} else {
				throw new Exception("=================== No Functionality selected for execution");
			}

		} else {
			throw new Exception("=================== Error while initializing Infrastructure.");
		}

	}

	/**
	 * getRequirementFile method is used to read requirement file and initialize
	 * the requirement file lookup map.
	 * 
	 * @author E880579
	 * @return JSONObject
	 */

	private JSONObject getRequirementFile() throws Exception {

		JSONObject requiredRunParameter = new JSONObject();

		try {
			requiredRunParameter = RequirementFileUtils.getRequirementFileNew();
		} catch (Exception e) {
			FrameworkGlobalVariables.logger4J.logError(FrameworkUtils.getMessage(e));
			throw e;
		}

		return requiredRunParameter;
	}

	/**
	 * initializeInfrastructure method is used to create all the required folder
	 * structure for reporting and other purpose.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	private boolean initializeInfrastructure() throws Exception {
		boolean flag = true;

		MobileObject.updateObjectDefinitionFiles();
		WebObject.updateObjectDefinitionFiles();
		
		FrameworkGlobalVariables.SOLUTION_TYPE = SuiteConstants.getConstantValue(SuiteConstantTypes.TEST_SUITE,
				"Solution_Category");

		// Creating Custom Log file
		if (!FrameworkGlobalVariables.LOG_CREATED) {

			// Creating date in yyyy_MM_dd_kk_mm_ss format
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_kk_mm_ss");
			String todaysFolder;

			// Creating User provided Result Folder
			if (FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER.isEmpty()) {
				// User specific result folder already initialized
			} else {

				if (!FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER.endsWith("/")
						|| !FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER.endsWith("\\")) {

					FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER = FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER
							.concat("/");
				}
			}

			// Initializing Suite Name
			FrameworkGlobalVariables.TEST_RUN_NAME = FrameworkGlobalVariables.TEST_RUN_NAME.isEmpty()
					? SuiteConstants.getConstantValue(SuiteConstantTypes.TEST_SUITE, "Suite_Name")
					: FrameworkGlobalVariables.TEST_RUN_NAME;

			todaysFolder = String.format("%s_%s",
					FrameworkGlobalVariables.TEST_RUN_NAME.toString().replaceAll("\\s+", "_"), sdf.format(new Date()));

			File folder;

			// Creating result folders
			if (XLTVUtil.UPLOAD_TO_XLTV )//|| FrameworkGlobalVariables.PUBLISH_RESULT) {
			{
				FrameworkUtils.checkFrameworkVersionIsCorrect();
				
				ReportUploadUtility reportUtilities = new ReportUploadUtility(ArtifactType.REBOT, todaysFolder);

				FrameworkGlobalVariables.CURRENT_EXECUTION_SUB_FOLDER = reportUtilities.createFolderOnServer(todaysFolder);
												
				File propertiesFile = new File("src/test/resources/Properties/XRayConfiguration.properties");

				if (propertiesFile.exists()) {
					FrameworkGlobalVariables.XRAY_CONFIG = new Properties();
					try (FileInputStream fis = new FileInputStream(propertiesFile)) {
						FrameworkGlobalVariables.XRAY_CONFIG.load(fis);
					} catch (Exception e) {
						FrameworkGlobalVariables.logger4J.logger
						.error("Load XRay Config properties: " + e.getMessage());
					}
				}

//				try{
//					FrameworkGlobalVariables.XRAY_PLAN_ID = XRayUtils.createTestPlan();
//				}catch(Exception e){
//					System.out.println("Create Test Plan in XRAY failed. Message - " + e.getMessage());
//				}

			} else {
				folder = new File(String.format("%s%s", FrameworkGlobalVariables.REBOT_FOLDER, todaysFolder));
				int counter = 1;
				while (folder.exists()) {
					todaysFolder = String.format("%s%s_%s", FrameworkGlobalVariables.REBOT_FOLDER, todaysFolder,
							counter);
					folder = new File(todaysFolder);
					counter++;
				}

				FrameworkGlobalVariables.CURRENT_EXECUTION_SUB_FOLDER = todaysFolder;
				File propertiesFile = new File("src/test/resources/Properties/XRayConfiguration.properties");

				if (propertiesFile.exists()) {
					FrameworkGlobalVariables.XRAY_CONFIG = new Properties();
					try (FileInputStream fis = new FileInputStream(propertiesFile)) {
						FrameworkGlobalVariables.XRAY_CONFIG.load(fis);
					} catch (Exception e) {
						FrameworkGlobalVariables.logger4J.logger
						.error("Load XRay Config properties: " + e.getMessage());
					}
				}
			}

			todaysFolder = (FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER.isEmpty()
					? FrameworkUtils.getFrameworkConstant("RESULTS_FOLDER")
					: FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER) + todaysFolder;

			folder = new File(todaysFolder);

			if (folder.mkdirs()) {

				String rebotFolder = String.format("%s/rebot_%s", todaysFolder,
						FrameworkGlobalVariables.CURRENT_EXECUTION_SUB_FOLDER);
				folder = new File(rebotFolder);
				folder.mkdirs();
				FrameworkGlobalVariables.REBOT_FOLDER = rebotFolder;
				String rebotRetryFolder = String.format("%s/retryResults_%s", todaysFolder,
						FrameworkGlobalVariables.CURRENT_EXECUTION_SUB_FOLDER);
				folder = new File(rebotRetryFolder);
				folder.mkdirs();
				FrameworkGlobalVariables.REBOT_RETRY_FOLDER = rebotRetryFolder;

				// Creating tests folder
				folder = new File(FrameworkGlobalVariables.REBOT_FOLDER + "/tests");

				folder.mkdir();

				folder = new File(FrameworkGlobalVariables.REBOT_FOLDER + "/retried_tests");

				folder.mkdir();

				// Creating Output.xml file
				folder = new File(FrameworkGlobalVariables.REBOT_FOLDER + "/output.xml");
				try {
					if (folder.createNewFile()) {

					} else {
						System.out.println("Suite Utils : Not able to create Output.xml file for Rebot framework.");
					}
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
				}

				// Creating Output.xml file
				folder = new File(FrameworkGlobalVariables.REBOT_FOLDER + "/output_retry.xml");
				try {
					if (folder.createNewFile()) {

					} else {
						System.out
								.println("Suite Utils : Not able to create Output_retry.xml file for Rebot framework.");
					}
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
				}

				// Creating Logs folder
				folder = new File(todaysFolder + "/logs");
				folder.mkdirs();
				FrameworkGlobalVariables.LOGS_FLD = todaysFolder + "/logs";

				// Creating Screen shots folder
				folder = new File(todaysFolder + "/screenShots");
				folder.mkdirs();
				FrameworkGlobalVariables.SCRSHOTS_FLD = todaysFolder + "/screenShots/";
			}

			FrameworkGlobalVariables.LOG_CREATED = true;
		}

		return flag;
	}

	public static int commandLineGroupParser(String[] commandLineArguments, int currentIndex) {
		++currentIndex;
		if (currentIndex < commandLineArguments.length) {

			String groupName = commandLineArguments[currentIndex];
			while (!groupName.startsWith(FrameworkUtils.getFrameworkConstant("DOUBLE_DASH"))) {
				FrameworkGlobalVariables.GroupSelected.add(groupName);
				++currentIndex;
				if (currentIndex == commandLineArguments.length) {
					break;
				} else {
					groupName = commandLineArguments[currentIndex];
				}
			}
		}
		--currentIndex;
		return currentIndex;
	}

	public static int commandLineTestRunNameParser(String[] commandLineArguments, int currentIndex) {
		++currentIndex;
		if (currentIndex < commandLineArguments.length) {

			String testRunName = commandLineArguments[currentIndex].trim();
			if (testRunName.isEmpty() && testRunName.startsWith(FrameworkUtils.getFrameworkConstant("DOUBLE_DASH"))) {
				System.out.println("TestRunName parameter needs value 'Name'");
				--currentIndex;
			} else {
				FrameworkGlobalVariables.TEST_RUN_NAME = testRunName;
			}
		}

		return currentIndex;
	}
	
	/**
	 * getTestSuite method is used to create instance of the suite and accepts
	 * different flags. It returns SuiteUtils instance created based on
	 * arguments passed.
	 * 
	 * @param commandLineArguments
	 *            String[] type variable that represents the command prompt
	 *            arguments.
	 * 
	 * 
	 * @author E880579
	 * @return SuiteUtils
	 */

	public static SuiteUtils getTestSuite(String[] commandLineArguments) throws Exception {

		FrameworkGlobalVariables.logger4J = Log4jLogger.getLogger();

		SUITE_RUN_ID = UUID.randomUUID().toString();

		ArrayList<String> newGroup = new ArrayList<String>();
		if (suite != null) {
			return suite;
		} else {

			try {

				// Reading Requirement file name from Suite properties file
				FrameworkGlobalVariables.REQUIREMENT_FILE_NAME = SuiteConstants
						.getConstantValue(SuiteConstantTypes.TEST_SUITE, "Requirment_File_Name");

				// Initializing framework jar version
				Package packages = FrameworkGlobalVariables.class.getPackage();

				FrameworkGlobalVariables.FRAMEWORK_VERSION = packages.getImplementationTitle() + " - "
						+ packages.getImplementationVersion();

			} catch (NullPointerException e) {
				FrameworkGlobalVariables.FRAMEWORK_VERSION = "Please check coreframework project's POM file";
			}

			if (FrameworkGlobalVariables.FRAMEWORK_VERSION == null) {
				FrameworkGlobalVariables.FRAMEWORK_VERSION = "Please check <version> tag in coreframework's POM.xml file";
			}

			int noOfCommands = commandLineArguments.length;

			if (noOfCommands == 0) {
				FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL = false;
				FrameworkGlobalVariables.USER_SPECIFIC_RESULT_FOLDER = FrameworkGlobalVariables.BLANK;
				FrameworkGlobalVariables.DOWNLOAD_VIDEOS_FROM_SAUCELABS = false;
			} else {
				
				try{
					CommandLineArguments.handleCommandLineArguments(commandLineArguments);
				}catch(Exception e){
					BDDSpecificCommandLineArgument.handleCommandLineArgument(commandLineArguments);
				}
			}
		}

		if (FrameworkGlobalVariables.APP_VERSION_TO_INSTALL.size() == 0) {
			throw new Exception("Test Suite - App version is mandatory parameter. Use --help to get details.");
		}

		commandLineArguments = new String[newGroup.size()];

		try {

			// Creating Suite Object.
			suite = new SuiteUtils();

		} catch (Exception e) {
			FrameworkGlobalVariables.logger4J
					.logFatal("Get Test Suite : Error Occured while initializing Test Suite - " + e.getMessage());
			throw e;
		}

		if (FrameworkGlobalVariables.PUBLISH_RESULT && FrameworkGlobalVariables.PERFORMANCE_RESULTUPLOAD) {
			FrameworkGlobalVariables.perfDBUtils = new PerformanceDatabaseUtils();
			FrameworkGlobalVariables.perfDBUtils.getDBConnection();
		}

		return suite;
	}

	/**
	 * executeSuite method starts the execution of test suite. Returns boolean
	 * type variable representing the execution started or not.
	 * 
	 * @author E880579
	 * @return Object
	 * @throws ParserConfigurationException
	 */

	public boolean executeSuite() throws IOException, ParserConfigurationException {

		if (xSuites.size() > 0) {

			FrameworkGlobalVariables.mapper = new KeywordClassMapper();
			if (FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL) {
				suite.runTestNGMultipleSuites();
			} else {
				suite.runTestNGSingleSuite();
			}
			
			RebotReportXML.generateReport();

			return true;
		} else {
			return false;
		}
	}

	public int runTestNGMultipleSuites() throws IOException {

		Iterator<String> iter = FrameworkGlobalVariables.GroupSelected.iterator();
		boolean isLocalExecution = false;

		HashSet<String> uniqueDeviceRequirement = new HashSet<>();
		JSONArray groupReqs;
		JSONObject object;
		JSONObject targetObject;
		Iterator<String> targets;
		String uniqueDevice;
		int status = Reporting.PASS;

		while (iter.hasNext()) {
			groupReqs = (JSONArray) FrameworkGlobalVariables.requirementLookupTable.get(iter.next());

			for (int counter = 0; counter < groupReqs.length(); ++counter) {
				object = groupReqs.getJSONObject(counter);
				targets = object.keys();
				while (targets.hasNext()) {
					targetObject = object.getJSONObject(targets.next());
					if (targetObject.has("Location")) {
						if ("Local".equalsIgnoreCase(targetObject.getString("Location"))) {
							isLocalExecution = true;
							uniqueDevice = targetObject.getString("OS") + targetObject.getString("Version")
									+ targetObject.getString("Name");
							if (uniqueDeviceRequirement.contains(uniqueDevice)) {
							} else {
								uniqueDeviceRequirement.add(uniqueDevice);
							}
						}
					}
				}
			}
		}

		TestRunner[] testRunners = null;

		if (isLocalExecution) {

			testRunners = new TestRunner[uniqueDeviceRequirement.size()];

			for (int index = 0; index < xSuites.size();) {
				for (int index1 = 0; index1 < uniqueDeviceRequirement.size(); ++index1, ++index) {
					testRunners[index1] = new TestRunner(xSuites.get(index + index1),
							FrameworkGlobalVariables.TEST_RUN_NAME);
					testRunners[index1].start();

					synchronized (testRunners) {
						if (status == Reporting.PASS) {
							status = testRunners[index1].getStatus();
						}
					}
				}

				try {
					for (int index1 = 0; index1 < uniqueDeviceRequirement.size(); ++index1) {
						testRunners[index1].join();
					}
				} catch (Exception e) {

				}
			}

		} else {
			testRunners = new TestRunner[xSuites.size()];

			for (int index = 0; index < xSuites.size(); ++index) {

				testRunners[index] = new TestRunner(xSuites.get(index), FrameworkGlobalVariables.TEST_RUN_NAME);
				testRunners[index].start();

				synchronized (testRunners) {
					if (status == Reporting.PASS) {
						status = testRunners[index].getStatus();
					}
				}
			}

			try {
				for (int index = 0; index < xSuites.size(); ++index) {
					testRunners[index].join();
				}
			} catch (InterruptedException e) {
				FrameworkGlobalVariables.logger4J.logError(FrameworkUtils.getMessage(e));
			}
		}

		return status;
	}

	class TestRunner extends Thread {

		private XmlSuite suite;
		private String suiteName;
		private int status;

		public TestRunner(XmlSuite suite, String suiteName) {
			this.suite = suite;
			this.suiteName = suiteName;
		}

		public int getStatus() {
			return status;
		}

		@Override
		public void run() {

			// Executing TestNG tests
			TestNG testNG = new TestNG();
			testNG.setDefaultSuiteName(suiteName);
			ArrayList<XmlSuite> xSuite = new ArrayList<>();
			xSuite.add(suite);
			testNG.setXmlSuites(xSuite);
			testNG.setVerbose(-1); // this fixes the NPE
			testNG.run();
			status = testNG.getStatus();
		}

	}

	public int runTestNGSingleSuite() throws IOException {

		// Executing TestNG tests
		TestNG testNG = new TestNG();
		testNG.setDefaultSuiteName(FrameworkGlobalVariables.TEST_RUN_NAME);
		testNG.setXmlSuites(xSuites);
		testNG.setVerbose(-1); // this fixes the NPE
		testNG.run();
		return testNG.getStatus();

	}

	public void getTagsLocation(ArrayList<String> includedFunctionality) {

		HashSet<String> uniqueIncludedFunctionality = new HashSet<>(includedFunctionality);

		Iterator<String> iter = uniqueIncludedFunctionality.iterator();

		TagsFinder[] tagsFinder = new TagsFinder[uniqueIncludedFunctionality.size()];
		int threadCounter = 0;
		while (iter.hasNext()) {
			tagsFinder[threadCounter] = new TagsFinder(iter.next());
			tagsFinder[threadCounter].start();
			++threadCounter;
		}

		for (TagsFinder tFinder : tagsFinder) {
			try {
				tFinder.join();
				BDDFrameworkGlobalVariable.tagMapper.put(tFinder.getTag(), tFinder.getFeatureFileNames());
			} catch (InterruptedException e) {
				FrameworkGlobalVariables.logger4J.logError("Get Tag Location : Error while getting tag location "
						+ tFinder.getTag() + ". Error message - " + e.getMessage());
			}
		}

	}

	public static Properties getProperties() {
		Properties prop = new Properties();
		return prop;
	}

}
