package com.demo.commons.coreframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.internal.Nullable;

import com.demo.commons.bddinterface.TestCaseMarkerClass;
import com.demo.commons.deviceCloudProviders.PCloudyExecutionDesiredCapability;
import com.demo.commons.devicepool.DevicePoolUtility;
import com.demo.commons.devicepool.LocalDevicePool;
import com.demo.commons.devicepool.NoDeviceFoundException;
import com.demo.commons.devicepool.UnExpectedException;
import com.demo.commons.mobile.CustomAndroidDriver;
import com.demo.commons.mobile.CustomDriver;
import com.demo.commons.mobile.CustomIOSDriver;
import com.demo.commons.mobile.Mobile;
import com.demo.commons.mobile.PostTestCompleted;
import com.demo.commons.perfecto.PerfectoConstants;
import com.demo.commons.perfecto.WindTunnelUtils;
import com.demo.commons.report.CustomLogger;
import com.demo.commons.report.FailType;
import com.demo.commons.report.XRayUtils;
import com.demo.commons.report.rebot.RebotReportUtils;
import com.demo.commons.report.rebot.XMLTestNode;
import com.demo.commons.web.WebObject;
import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.test.result.TestResultFactory;
import com.ssts.pcloudy.appium.PCloudyAppiumSession;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

@SuppressWarnings("unused")
public class TestCases implements TestCaseMarkerClass {

	private HashMap<String, CustomDriver> mobDrivers = new HashMap<String, CustomDriver>();
	private HashMap<String, WebDriver> webDrivers = new HashMap<String, WebDriver>();
	private CustomLogger logFile;
	private boolean isCritical;
	private boolean currentKeywordStatus = true;
	private String driverSessionID;
	public boolean isAcquisitionSuccessful = true;
	private TestCaseInputs inputs;
	private boolean isMobileTestCase;
	private boolean isWebTestCase;
	private String currentTargetName;
	private String scrShotPath;
	private XMLTestNode xmlTestNode;
	private int screenShotCount = 0;
	private String currentNetworkName = FrameworkGlobalVariables.NORMAL_WIFI;
	public String keywordType;
	private HashMap<String, Long> timers = new HashMap<String, Long>();
	private boolean hasFalsePositive = false;
	private String reasonForFalsePositive = FrameworkGlobalVariables.BLANK;
	private ArrayList<String> resourceIDs = new ArrayList<>();
	private HashMap<String, HashMap<String, String>> mobileInformation = new HashMap<String, HashMap<String, String>>();
	private int deviceIndex = 0;
	private boolean skipExecution = false;
	private PerfectoExecutionContext executionContext;
	private ReportiumClient reportiumClient;
	private String firstErrorMessage = FrameworkGlobalVariables.BLANK;
	private long resourceWaitTime=0;
	private boolean useXCUI = FrameworkGlobalVariables.SWITCH_TO_XCUITEST;
	private HashMap<String,PCloudyExecutionDesiredCapability.PCloudyDeviceInformation> pcloudyBookedDeviceIDs;
	private LocalDevicePool localDevicePool;
	private HashMap<String, WebObject> fieldObjects;
	private PCloudyAppiumSession pCloudySession;
	private String startTimeXRayTest;
	private String xRayTestID = FrameworkGlobalVariables.BLANK;
	private ArrayList<String> appVersionUsed;
	private String testXmlFileName;
	private String testExecutionID = FrameworkGlobalVariables.BLANK;
	private HashMap<String, ArrayList<String>> customLogs = new HashMap<>();
	
	public HashMap<String, ArrayList<String>> getCustomLogs(){
		return customLogs;
	}
	
	public void setTestExecutionID(String testExecID){
		this.testExecutionID = testExecID;
	}
	
	public String getTestExecutionID(){
		return this.testExecutionID;
	}

	public String getTestXmlFileName() {
		return testXmlFileName;
	}

	public void setTestXmlFileName(String testXmlFileName) {
		this.testXmlFileName = testXmlFileName;
	}
	
	public PCloudyAppiumSession getpCloudySession() {
		return pCloudySession;
	}
	
	public void addAppVersion(String appVersion){
		if(appVersionUsed==null){
			appVersionUsed = new ArrayList<>();
		}
		appVersionUsed.add(appVersion);
	}
	
	public Iterator<String> getAppVersions(){
		return appVersionUsed.iterator();
	}
	
	public String getXRayTestID() {
		return xRayTestID;
	}

	public void setXRayTestID(String xRayTestID) {
		if(FrameworkGlobalVariables.PUBLISH_RESULT){
			this.xRayTestID = xRayTestID;
//			XRayUtils.addTestToTestPlan(this, xRayTestID);
		}
	}
	
	public String getStartTimeXRayTest() {
		return startTimeXRayTest;
	}

	public void setStartTimeXRayTest(String startTimeXRayTest) {
		this.startTimeXRayTest = startTimeXRayTest;
	}
	
	public void setpCloudySession(PCloudyAppiumSession pCloudySession) {
		this.pCloudySession = pCloudySession;
	}
	
	public void setObjectFile(HashMap<String, WebObject> fieldObjects){
		this.fieldObjects=fieldObjects;
	}
	
	public HashMap<String, WebObject> getObjectFile(){
		return fieldObjects;
	}


	private String readClassNames(String fileName,String providerName){
		
		Properties properties = new Properties();
		
		StringBuilder propertyFileName = new StringBuilder(FrameworkGlobalVariables.DEVICE_PROVIDER_FLD);
		propertyFileName = propertyFileName.append(fileName);
		
		try(InputStream fis = FrameworkUtils.class.getResourceAsStream(propertyFileName.toString())){
			properties.load(fis);

			if(properties.containsKey(providerName.trim())){
				return properties.getProperty(providerName);
			}else{
				return FrameworkGlobalVariables.BLANK;
			}
			
		} catch (IOException e) {
			FrameworkGlobalVariables.logger4J.logFatal(String.format(
					"Error occured while reading %s key from Framework Constants file file. Error - %s"
					, providerName,e.getMessage()));
			
			return FrameworkGlobalVariables.BLANK;
		}finally{
			properties = null;
			propertyFileName = null;
		}
	}
	
	public LocalDevicePool getLocalDevicePool() {
		return localDevicePool;
	}

	public void setLocalDevicePool(LocalDevicePool localDevicePool) {
		this.localDevicePool = localDevicePool;
	}

	public void setPcloudyDeviceInformation(PCloudyExecutionDesiredCapability.PCloudyDeviceInformation deviceInformation){
		if(pcloudyBookedDeviceIDs!=null){
			pcloudyBookedDeviceIDs.put(inputs.getCurrentTargetName(), deviceInformation);
		}else{
			pcloudyBookedDeviceIDs = new HashMap<String, PCloudyExecutionDesiredCapability.PCloudyDeviceInformation>();
			pcloudyBookedDeviceIDs.put(inputs.getCurrentTargetName(), deviceInformation);
		}
	}
	
	public PCloudyExecutionDesiredCapability.PCloudyDeviceInformation getPcloudyDeviceInformation(){
		if(pcloudyBookedDeviceIDs!=null){
			if(pcloudyBookedDeviceIDs.containsKey(inputs.getCurrentTargetName())){
				return pcloudyBookedDeviceIDs.get(inputs.getCurrentTargetName());
			}else{
				Keyword.ReportStep_Fail_WithOut_ScreenShot(this, FailType.FALSE_POSITIVE, 
						readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, ErrorMessages.ERR_3));
				return null;
			}
			
		}else{
			Keyword.ReportStep_Fail_WithOut_ScreenShot(this, FailType.FALSE_POSITIVE,
					readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, ErrorMessages.ERR_4));
			return null;
		}
	}
	
	public Set<String> getAllPcloudyInstanceName(){
		if(pcloudyBookedDeviceIDs!=null){
			return pcloudyBookedDeviceIDs.keySet();
		}else{
			return new HashSet<>();
		}
	}

	public long getResourceWaitTime() {
		return resourceWaitTime;
	}
	
	public boolean doUseXCUITest(){
		return useXCUI;
	}
	
	public void useXCUITest(boolean flag){
		useXCUI = flag;
	}

	public void setResourceWaitTime1(long resourceWaitTime) {
		this.resourceWaitTime = resourceWaitTime;
	}
	
	public void copyMobileInformation(HashMap<String, HashMap<String, String>> mobileInformation) {
		this.mobileInformation = mobileInformation;
	}
	
	public HashMap<String, HashMap<String, String>> getMobileInformation() {
		return mobileInformation;
	}

	public String getFirstErrorMessage() {
		return firstErrorMessage;
	}

	public void setFirstErrorMessage(String firstErrorMessage) {
		this.firstErrorMessage = firstErrorMessage;
	}

	public void updatePerfectoTestUpdate() {
		if (inputs.isRunningOn(PerfectoConstants.PerfectoConstant.PERFECTO.getPerfectoConstant())) {
			try{
				
				if(getReportiumClient()!=null){
					
					inputs.setInputValueWithoutTarget(PerfectoConstants.PERFECTO_RESULT_URL, 
							getReportiumClient().getReportUrl(),false);
					
					getReportiumClient().testStop(isTestSuccessful() ? TestResultFactory.createSuccess()
							: TestResultFactory.createFailure(firstErrorMessage, null));
				}else{
					switch (inputs.getInputValue(TestCaseInputs.OS_NAME).toUpperCase()) {
					case Mobile.ANDROID:		
						
						if(inputs.isInputAvailable(PerfectoConstants.PERFECTO_RESULT_URL)){
							// Report url is already collected
						}else{
							inputs.setInputValueWithoutTarget(PerfectoConstants.PERFECTO_RESULT_URL,
									((CustomAndroidDriver) getMobileDriver())
									.getCapabilities().getCapability(WindTunnelUtils.WIND_TUNNEL_REPORT_URL_CAPABILITY));
						}
						break;
					case Mobile.IOS:
						
						if(inputs.isInputAvailable(PerfectoConstants.PERFECTO_RESULT_URL)){
							// Report url is already collected
						}else{
							inputs.setInputValueWithoutTarget(PerfectoConstants.PERFECTO_RESULT_URL, ((CustomIOSDriver) getMobileDriver())
									.getCapabilities().getCapability(WindTunnelUtils.WIND_TUNNEL_REPORT_URL_CAPABILITY));
						}

						break;
					}
				}
				
			}catch(UnreachableBrowserException e){
				Keyword.ReportStep_Fail_WithOut_ScreenShot(this, FailType.FALSE_POSITIVE, 
						readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, ErrorMessages.ERR_5));
			}catch(Exception e){
				
				StringBuilder errMsg = new StringBuilder(readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, ErrorMessages.ERR_6));
				errMsg = errMsg.append(FrameworkUtils.getMessage(e));
				Keyword.ReportStep_Fail_WithOut_ScreenShot(this, FailType.FALSE_POSITIVE,
						 errMsg.toString());
				errMsg = null;
			}
		}
	}

	public void setTestCaseInput(TestCaseInputs inputs){
		this.inputs = inputs;
	}
	
	
	public ReportiumClient getReportiumClient() {
		return reportiumClient;
	}

	public void setReportiumClient(ReportiumClient reportiumClient) {
		this.reportiumClient = reportiumClient;
	}

	public void setPerfectoExecutionContext(PerfectoExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public PerfectoExecutionContext getPerfectoExecutionContext() {
		return executionContext;
	}

	public boolean isSkipExecution() {
		return skipExecution;
	}

	public void setSkipExecution(boolean skipExecution) {
		this.skipExecution = skipExecution;
	}

	private String execToken = FrameworkGlobalVariables.BLANK;
	private String mDeviceToken = FrameworkGlobalVariables.BLANK;

	public String getMobileTokenKey() {
		return mDeviceToken.toUpperCase();
	}

	public String getExecutionTokenKey() {
		return execToken.toUpperCase();
	}

	public void setTokenKey(String tokenID) {
		this.execToken = tokenID.toUpperCase();
	}
	
	public void setMobileIndex(int deviceIndex) {
		this.deviceIndex = deviceIndex;
	}

	public int getMobileIndex() {
		return deviceIndex;
	}

	public ArrayList<String> getResourceIDs() {
		return resourceIDs;
	}

	public void setResourceIDs(ArrayList<String> resourceIDs) {
		this.resourceIDs = resourceIDs;
	}

	public HashMap<String, String> getMobileInformation(String targetName) {
		return mobileInformation.get(targetName);
	}

	public void setMobileInformation(HashMap<String, HashMap<String, String>> mobInformation) {
		this.mobileInformation = mobInformation;
	}

	public void setFalsePositive(String falsePositiveMessage) {

		hasFalsePositive = true;
		if (reasonForFalsePositive.isEmpty()) {
			reasonForFalsePositive = falsePositiveMessage;// .toUpperCase();
		}
	}

	public boolean hasFalsePositive() {
		return hasFalsePositive;
	}

	public String getFalsePositiveReason() {
		return reasonForFalsePositive;
	}

	public void startTimer(String timerName) {
		
		StringBuilder message = new StringBuilder(readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, InfoMessages.MSG_1));
		message = message.append(timerName);
		
		Keyword.ReportStep_Pass(this, message.toString() );
		timers.put(timerName.toLowerCase(), System.currentTimeMillis());
		message = null;
	}

	public long stopTimer(String timerName, String informType, String informName, String hwDeviceName) {

		timerName = timerName.toLowerCase();
		if (timers.containsKey(timerName)) {
			long difference = System.currentTimeMillis() - timers.get(timerName);

			if (FrameworkGlobalVariables.perfDBUtils != null) {
				FrameworkGlobalVariables.perfDBUtils.writeData(this, informType, informName, String.valueOf(difference),
						"msec", hwDeviceName);
			}
			
			StringBuilder message = new StringBuilder(readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, InfoMessages.MSG_2));
			message = message.append(timerName);
			message = message.append(" ");
			message = message.append(difference);

			Keyword.ReportStep_Pass(this,message.toString());
			
			message = null;

			timers.remove(timerName);
			return difference;
		} else {
			StringBuilder message = new StringBuilder(readClassNames(FrameworkGlobalVariables.TESTCASES_PROPERTIES, InfoMessages.MSG_3));
			message = message.append(timerName);
			
			Keyword.ReportStep_Pass(this, message.toString());
			return -1;
		}
	}

	public int getScreenShotCount() {
		return screenShotCount;
	}

	public String getCurrentNetworkName() {
		return this.currentNetworkName;
	}

	public void setCurrentNetworkName(String networkName) {
		this.currentNetworkName = networkName;
	}

	public void incrementScreenShotCount() {
		++screenShotCount;
	}

	public boolean isTestSuccessful() {
		return xmlTestNode.isTestSuccess();
	}

	public String getDriverSessionID() {
		return this.driverSessionID;
	}

	public void setDriverSessionID(String driverSessionID) {
		this.driverSessionID = driverSessionID;
	}

	public boolean isCritical() {
		return isCritical;
	}

	public boolean isWebTestCase() {
		return isWebTestCase;
	}

	public void setIsWebTestCase(boolean isWebTestCase) {
		this.isWebTestCase = isWebTestCase;
	}

	public boolean isMobileTestCase() {
		return isMobileTestCase;
	}

	public void setIsMobileTestCase(boolean isMobileTestCase) {
		this.isMobileTestCase = isMobileTestCase;
	}

	public void setCurrentKeywordStatus(boolean currentKeywordStatus) {
		this.currentKeywordStatus = currentKeywordStatus;
		if(xmlTestNode.isTestSuccess()){
			xmlTestNode.setTestSuccess(currentKeywordStatus);
		}
		
	}

	public void setCritical(boolean isCritical) {
		this.isCritical = isCritical;
	}

	public XMLTestNode getXMLTestNode() {
		return xmlTestNode;
	}

	public String getDeviceName() {
		return inputs.getInputValue(TestCaseInputs.DEVICE_NAME);
	}

	public static TestCases initializeTestCase(TestCaseInputs inputs) {
		return new TestCases(inputs);
	}

	public TestCaseInputs getTestCaseInputs() {
		return inputs;
	}

	private TestCases(TestCaseInputs inputs) {

		this.inputs = inputs;
		this.startTimeXRayTest = XRayUtils.getTimeStamp();
		StringBuilder testCaseName = new StringBuilder(inputs.getInputValue(FrameworkGlobalVariables.TESTCASE_NAME));
		testCaseName = testCaseName.append((inputs.getInputValue("index").equals("0")?"": " - #" + (Integer.valueOf(inputs.getInputValue("index"))+1)));
		
		logFile = new CustomLogger(testCaseName.toString());
		
		try {
			xmlTestNode = new XMLTestNode(testCaseName.toString());
			testXmlFileName = xmlTestNode.getXmlFileName();
		} catch (ParserConfigurationException e1) {
			FrameworkGlobalVariables.logger4J.logFatal(e1.getLocalizedMessage());
		}

		logFile.startLogging();
		if (FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL) {
			// Nothing to DO
		} else {

			if (mobDrivers != null) {
				Set<String> keys = mobDrivers.keySet();

				Iterator<String> iter = keys.iterator();
				String key;
				while (iter.hasNext()) {
					key = iter.next();
					CustomDriver driver = mobDrivers.get(key);
					try {
						driver.closeApp();
						driver.quit();
					} catch (Exception e) {
					}
					mobDrivers.remove(key);
				}
			}

			if (webDrivers != null) {
				Set<String> keys = webDrivers.keySet();

				Iterator<String> iter = keys.iterator();
				String key;
				while (iter.hasNext()) {
					key = iter.next();
					WebDriver driver = webDrivers.get(key);
					try {
						driver.close();
						driver.quit();
					} catch (Exception e) {
					}
					mobDrivers.remove(key);
				}
			}

		}

		mDeviceToken = execToken;
		
		if (inputs.isAccountAquisitionAutomatic()) {

			if (FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL) {
				// Nothing to Do
				
			} else {
				getAccountfromResourcePool();
			}

		} else {
			
			// commented to avoid device pool verification for account and mobile device #DATE 05-09-2020

//			if (FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL) 
//			{
//				// Nothing to Do
//			} 
//			else {
//				if (inputs.doCheckDevicePoolForMobile()) {
//					try {
//						getAccountfromResourcePool();
//					} catch (Exception e) {
//						hasFalsePositive = true;
//						StringBuilder message = new StringBuilder("Mobile Device - ");
//						message = message.append(FrameworkUtils.getMessage(e));
//						reasonForFalsePositive = message.toString();
//						message = null;
//						FrameworkGlobalVariables.logger4J.logFatal(reasonForFalsePositive);
//					}
//				} else {
//					// No need to do any thing.
//				}
//			}

			if (hasFalsePositive) 
			{
				xmlTestNode.setTestSuccess(false);
			}
		}

		synchronized (this) {
			scrShotPath = RebotReportUtils.createScreenShotFolder(FrameworkGlobalVariables.TESTCASE_NAME);
		}

		// xmlTestNode.addTags(inputs, this);
	}

	public String getScrShotPath() {
		return scrShotPath;
	}

	public String getPlatform() {
		return inputs.getInputValue(TestCaseInputs.SCREEN_SIZE);
	}

	public void setRunningOnRealDevice(boolean isRealDevice) {
		inputs.setIsRealDevice(isRealDevice);
	}

	@Deprecated
	public CustomDriver getDriver() {
		return getMobileDriver();
	}

	@Deprecated
	public void setDriver(@Nullable CustomDriver driver) {
		mobDrivers.put(inputs.getCurrentTargetName(), driver);
		isMobileTestCase = true;
	}

	public CustomDriver getMobileDriver() {
		return mobDrivers.get(inputs.getCurrentTargetName());
	}

	public HashMap<String, CustomDriver> getMobileDrivers() {
		return mobDrivers;
	}

	public WebDriver getWebDriver() {
		return webDrivers.get(inputs.getCurrentTargetName());
	}

	public void setMobileDriver(@Nullable CustomDriver driver) {
		
		if(driver==null){
			RebotReportUtils.cleanSession(this, getMobileDriver());
		}
		mobDrivers.put(inputs.getCurrentTargetName(), driver);
		isMobileTestCase = true;
	}

	public void setWebDriver(@Nullable WebDriver driver) {
		webDrivers.put(inputs.getCurrentTargetName(), driver);
		isWebTestCase = true;
	}

	public String getTestCaseName() {
		return inputs.getInputValue(FrameworkGlobalVariables.TESTCASE_NAME);
	}

	public CustomLogger getLogFile() {
		return logFile;
	}

	public void testCaseCompleted() throws Exception {

		xmlTestNode.addTestStatus();
		
		if(localDevicePool!=null){
			localDevicePool.releaseTestAccount();
		}

		logFile.stopLogging();
		
		if(inputs.getInputValue(TestCaseInputs.EXEC_LOCATION).equalsIgnoreCase("Local")){
			
		}else{
			try {
				Class<?> clazz = Class.forName(readClassNames(FrameworkGlobalVariables.POST_TC_FILE, 
						inputs.getInputValue(TestCaseInputs.EXEC_LOCATION)));
						
				PostTestCompleted testSteps = (PostTestCompleted) clazz.newInstance();
				testSteps.stepsToPerform(this, xmlTestNode.isTestSuccess());
			}catch(ClassNotFoundException e){
				FrameworkGlobalVariables.logger4J.logError("Post Test Case Step : Post Test Case class not defined.");
			}catch (Exception e) {
				FrameworkGlobalVariables.logger4J.logError("Post Test Case Step :" + e.getMessage());
			}
		}
		
		xmlTestNode.addForTag(this);
		xmlTestNode.commitTest(this);
		
		TestCaseRetryMechanism.moveTestResultToRetryFolder(this);

		if (FrameworkGlobalVariables.INSTANTIATE_DRIVER_GROUP_LEVEL) {
			// Do nothing - Driver will be released in Test Case Executor.
		} else {
			if (mobDrivers != null) {
				Set<String> keys = mobDrivers.keySet();

				Iterator<String> iter = keys.iterator();
				String key;
				while (iter.hasNext()) {
					key = iter.next();
					CustomDriver driver = mobDrivers.get(key);
					try{
						if(driver!=null){
							driver.quit();
						}
					}catch(Exception e){
						FrameworkGlobalVariables.logger4J.logError("Error while calling driver.quit : Error Message " 
								+ e.getMessage());
					}
				}
			}

			if (webDrivers != null) {
				Set<String> keys = webDrivers.keySet();

				Iterator<String> iter = keys.iterator();
				String key;
				while (iter.hasNext()) {
					key = iter.next();
					WebDriver driver = webDrivers.get(key);
					try{
						if(driver!=null){
							driver.quit();
						}
						
					}catch(Exception e){
						FrameworkGlobalVariables.logger4J.logError("Error while calling driver.quit : Error Message " 
								+ e.getMessage());
					}
				}
			}

		}
	}

	public void completedSetup() {
		// xmlTestNode.addSetUpStatus();
		xmlTestNode.commitTest(this);
	}

	public void completedActualTest() {
		// xmlTestNode.addActualTestStatus();
		xmlTestNode.commitTest(this);
	}

	public void completedTearDown() {

	}

	public void getAccountfromResourcePool() {

		String id = FrameworkGlobalVariables.NO_ACCOUNT;
		String testAccount = inputs.getInputValue(TestCaseInputs.TAGS);

		HashMap<String, Object> mobTokenInputs = new HashMap<>();

		try {
			
			long startTime = System.currentTimeMillis();
			
			JSONArray deviceInformation = DevicePoolUtility.getAvailableResources(inputs, testAccount,
					inputs.getInputValue("TESTCASENAME").toString().replaceAll("#", FrameworkGlobalVariables.BLANK).replaceAll("\\s+", FrameworkGlobalVariables.BLANK));

			resourceWaitTime = System.currentTimeMillis() - startTime;
			
			if (deviceInformation.getJSONObject(0).has("ErrorCode")) {

				switch (deviceInformation.getJSONObject(0).getInt("ErrorCode")) {
				case 404:
					throw new NoDeviceFoundException(deviceInformation.getJSONObject(0).getString("ErrorMessage") + " with tags : " + testAccount.split("tags\":")[1].replace("}", ""));
				case 0:
				case 500:
				default:
					throw new UnExpectedException(deviceInformation.getJSONObject(0).getString("ErrorMessage") + " for tags : " + testAccount);
				}
			}

			ArrayList<HashMap<String, String>> resInformations = DevicePoolUtility.getResouceInformation(this, deviceInformation);

			for (int index = 0; index < resInformations.size(); ++index) {

				inputs.switchToTarget(RequirementFileUtils.TARGET_PREFIX + (index + 1));
				HashMap<String, String> resInformation = resInformations.get(index);
				Set<String> properties = resInformation.keySet();

				Iterator<String> propIter = properties.iterator();
				while (propIter.hasNext()) {
					String propName = propIter.next();
					inputs.setInputValue(propName, resInformation.get(propName));
				}
			}

			inputs.switchToDefaultTarget();

		} catch (NoDeviceFoundException e) {
			hasFalsePositive = true;
			reasonForFalsePositive = "Resource Pool - " + e.getLocalizedMessage();
			if (xmlTestNode != null) {
				xmlTestNode.setTestSuccess(false);
			}
			FrameworkGlobalVariables.logger4J.logFatal(reasonForFalsePositive);
			isAcquisitionSuccessful = false;
		} catch (Exception e) {
			hasFalsePositive = true;
			reasonForFalsePositive = "Resource Pool - " + e.getLocalizedMessage();
			if (xmlTestNode != null) {
				xmlTestNode.setTestSuccess(false);
			}
			FrameworkGlobalVariables.logger4J.logFatal(reasonForFalsePositive);
			isAcquisitionSuccessful = false;
		} finally {
			inputs.setInputValue(TestCaseInputs.ACCOUNT_ID, id);
		}
	}
	
	/*public void setRetryCount(int count)
	{
		retryCount=count;
	}

	public int getRetryCount()
	{
		return retryCount;
	}*/
	
}