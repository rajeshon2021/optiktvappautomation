package com.honeywell.commons.mobile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.CheckForNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;
import com.honeywell.commons.coreframework.FrameworkGlobalVariables;
import com.honeywell.commons.coreframework.FrameworkUtils;
import com.honeywell.commons.coreframework.Keyword;
import com.honeywell.commons.coreframework.TestCaseInputs;
import com.honeywell.commons.coreframework.TestCases;
import com.honeywell.commons.report.FailType;

import io.appium.java_client.MobileBy.ByAccessibilityId;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
//import io.appium.java_client.android.Connection;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.HideKeyboardStrategy;
import static io.appium.java_client.touch.offset.PointOption.point;
import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.ElementOption.element;
import static io.appium.java_client.touch.LongPressOptions.longPressOptions;
import static io.appium.java_client.touch.TapOptions.tapOptions;

/**
 * MobileUtils class represents interaction with Mobile Devices.
 * 
 * @author E880579
 * @version 1.0
 */

public class MobileUtils {
	
	
	/**
	 * switchToAppContext is use to Set driver's Context
	 * 
	 * 
	 * @param testCase
	 *            TestCases type variable represents TestCase.
	 * 
	 * @param context
	 *            String type variable represents context to get.
	 * 
	 * @author E880579
	 * 
	 */

	public static void switchToAppContext(TestCases testCase, String context) {
		CustomDriver driver = testCase.getMobileDriver();
		context = context.toUpperCase();

		if (driver == null) {
			return;
		} else {
			try{
				Set<String> contexts = driver.getContextHandles();
				
				if(contexts.contains(context)){
					driver.context(context);
				}else{
					Keyword.ReportStep_Fail(testCase, FailType.FALSE_POSITIVE, 
							String.format("Switch to App context: Context - %s is not present on Screen", context));
				}
			}catch(Exception e){
				Keyword.ReportStep_Fail(testCase, FailType.FALSE_POSITIVE, 
						String.format("Switch to App context: Context - %s is not set. Error occured - %s", context,e.getMessage()));
			}
		}
	}

	/**
	 * getCapability is use to Get driver's capability
	 * 
	 * It returns String type value representing capability.
	 * 
	 * @param testCase
	 *            TestCases type variable represents TestCase.
	 * 
	 * @param capabilityName
	 *            String type variable represents capability to get.
	 * 
	 * @author E880579
	 * @return Duration
	 */

	public static String getCapability(TestCases testCase, String capabilityName) {
		CustomDriver driver = testCase.getMobileDriver();

		if (driver == null) {
			return FrameworkGlobalVariables.BLANK;
		} else {
			Capabilities capability = null;
			if (isRunningOnAndroid(testCase)) {
				capability = ((CustomAndroidDriver) driver).getCapabilities();
			} else {
				capability = ((CustomIOSDriver) driver).getCapabilities();
			}

			if (capability.is(capabilityName)) {
				return (String) capability.getCapability(capabilityName);
			} else {
				return FrameworkGlobalVariables.BLANK;
			}
		}
	}

	/**
	 * getDimensionOfScreen is use to Get Mobile device screen size
	 * 
	 * It returns Dimension type value representing Dimension of screen.
	 * 
	 * @param testCase
	 *            TestCases type variable represents TestCase.
	 * 
	 * @author E880579
	 * @return Duration
	 */

	public static Dimension getDimensionOfScreen(TestCases testCase) {
		CustomDriver driver = testCase.getMobileDriver();
		if (driver == null) {
			return null;
		} else {
			return driver.manage().window().getSize();
		}

	}

	/**
	 * getLocationOfElement is use to Get Mobile Element's Location
	 * 
	 * It returns Point type value representing Mobile Element's Location
	 * 
	 * @param testCase
	 *            TestCases type variable represents TestCase.
	 * 
	 * @author E880579
	 * @return Duration
	 */

	public static Point getLocationOfElement(TestCases testCase, WebElement element) {
		if (element == null) {
			return null;
		} else {
			return element.getLocation();
		}

	}

	private static MobileElement scrollToAndroid(TestCases testCase, String label) {
		
		CustomAndroidDriver driver = (CustomAndroidDriver) testCase.getMobileDriver();
		
		if(driver!=null){
			String uiAutomator = 
					String.format("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionContains(\"%s\").instance(0));new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\"%s\").instance(0));", label,label);
			try{
				return driver.findElementByAndroidUIAutomator(uiAutomator);
			}catch(Exception e){
				return null;
			}
		}else{
			return null;
		}

		
	}
	
	public static MobileElement scrollToExactAndroid(TestCases testCase, String label) {

		String uiAutomator = 
				String.format("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().descriptionMatches(\"%s\").instance(0));new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textMatches(\"%s\").instance(0));", label,label);
			
		CustomAndroidDriver driver = (CustomAndroidDriver) testCase.getMobileDriver();
		
		try{
			return driver.findElementByAndroidUIAutomator(uiAutomator);
		}catch(Exception e){
			return null;
		}
	}

	private static MobileElement scrollToIOS(TestCases testCase, String label) {

		CustomIOSDriver driver = (CustomIOSDriver) testCase.getMobileDriver();

		if (driver != null) {
			try {
				FluentWait<CustomIOSDriver> fWait = new FluentWait<CustomIOSDriver>(driver);
				
				//fWait.withTimeout(1, TimeUnit.SECONDS);
				//fWait.pollingEvery(200, TimeUnit.MILLISECONDS);
				
				fWait.withTimeout(Duration.ofSeconds(1));
				fWait.pollingEvery(Duration.ofMillis(200));
				

				List<WebElement> element = fWait.until(
						ExpectedConditions.presenceOfAllElementsLocatedBy(ByAccessibilityId.AccessibilityId(label)));
				
				if (element.size() == 0) {
					Keyword.ReportStep_Fail(testCase, FailType.FALSE_POSITIVE,
							"Scroll to IOS : Element with Accessibility Label - " + label + " is not available.");
				} else {
					if (element.get(0).isDisplayed()) {
						return (MobileElement) element.get(0);
					} else {

						String xpathCells = String.format(
								"//*[@name='%s']/ancestor::*[contains(@type,'Table')]/*[contains(@type,'Cell') and @visible='true']",
								label);

						String xpathVisibleBelow = String.format(
								"//*[@name='%s']/following::*[contains(@type,'Cell') and @visible='true' and @name!='StandardLocationTrackingOn' and @value!='SSID']",
								label);

						String pageSource = driver.getPageSource();

						int direction = -1;

						int height = 0;
						int startY = 0;

						DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = null;
						try {
							builder = builderFactory.newDocumentBuilder();

							Document xmlDocument = builder.parse(new ByteArrayInputStream(pageSource.getBytes()));

							XPath xPath = XPathFactory.newInstance().newXPath();

							NodeList nodeCellsList = (NodeList) xPath.compile(xpathCells).evaluate(xmlDocument,
									XPathConstants.NODESET);

							NodeList nodeList = (NodeList) xPath.compile(xpathVisibleBelow).evaluate(xmlDocument,
									XPathConstants.NODESET);
							int elementsVisibleBelow = nodeList.getLength();

							Node firstCell = nodeCellsList.item(0);
							Node lastCell = nodeCellsList.item(nodeCellsList.getLength() - 1);

							if (elementsVisibleBelow > 0) {
								direction = 1;
								startY = Integer.parseInt(((Element) firstCell).getAttribute("y").toString());
								startY = startY
										+ Integer.parseInt(((Element) firstCell).getAttribute("height").toString()) + 2;
								height = Integer.parseInt(((Element) lastCell).getAttribute("y").toString());
							} else {
								startY = Integer.parseInt(((Element) lastCell).getAttribute("y").toString());
								height = startY;
								// Integer.parseInt(((Element)firstCell).getAttribute("y").toString());
							}

						} catch (ParserConfigurationException e) {
							FrameworkGlobalVariables.logger4J.logError(e.getMessage());
						}

						while (element.size() != 0) {
							if (MobileUtils.swipe(testCase, 5, startY, 5, (height * direction))) {
								element = fWait.until(ExpectedConditions
										.presenceOfAllElementsLocatedBy(ByAccessibilityId.AccessibilityId(label)));

								if (element.size() == 0) {
									break;
								} else {
									if (element.get(0).isDisplayed()) {
										return (MobileElement) element.get(0);
									}
								}
							} else {
								break;
							}
						}
					}
				}

			} catch (Exception e) {
				Keyword.ReportStep_Fail(testCase, FailType.FALSE_POSITIVE,
						"Scroll to IOS : Error occured. Message - " + e.getMessage());
			}
		}

		return null;
	}

	/**
	 * getDuration is use to convert duration in milliseconds to Duration object
	 * 
	 * It returns Duration type value representing Duration object.
	 * 
	 * @param durationInMilliSecond
	 *            long type variable that duration in milliseconds.
	 * 
	 * @author E880579
	 * @return Duration
	 */

	public static Duration getDuration(long durationInMilliSecond) {
		return Duration.between(Instant.now(), Instant.now().plusMillis(durationInMilliSecond));
	}

	/**
	 * swipe is use to perform swipe.
	 * 
	 * It returns Boolean type value representing action success.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param xStartCoordinate
	 *            Integer type variable represent start x coordinate.
	 * 
	 * @param yStartCoordinate
	 *            Integer type variable represent start y coordinate.
	 * 
	 * @param xEndCoordinate
	 *            Integer type variable represent End x coordinate.
	 * 
	 * @param yEndCoordinate
	 *            Integer type variable represent End y coordinate.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean swipe(TestCases testCase, int xStartCoordinate, int yStartCoordinate, int xEndCoordinate,
			int yEndCoordinate) {

		CustomDriver driver = testCase.getMobileDriver();

		StringBuilder message = new StringBuilder("Swipe : ");

		if (driver == null) {
		} else {
			try {
				TouchAction actions = new TouchAction(driver);
				//actions = actions.press(xStartCoordinate, yStartCoordinate).waitAction(getDuration(500)).moveTo(xEndCoordinate, yEndCoordinate).release().perform();
				actions = actions.press(point(xStartCoordinate, yStartCoordinate)).waitAction(waitOptions(MobileUtils.getDuration(500))).moveTo(point(xEndCoordinate, yEndCoordinate)).release().perform();
				
				if (actions == null) {
					message = message.append(", not able to perform swipe.");
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
					message = null;
				} else {
					message = message.append(", swipe press successful.");
					Keyword.ReportStep_Pass(testCase, message.toString());
					message = null;
					return true;
				}
			} catch (Exception e) {
				message = message.append(", not able to perform swipe. Reason - ");
				message = message.append(e.getMessage());
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
				message = null;
			}

		}

		return false;
	}
	
	/**
	 * swipe is use to perform swipe.
	 * 
	 * It returns Boolean type value representing action success.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param startElement
	 *            MobileElement type variable represent Start Element.
	 * 
	 * @param endElement
	 *            MobileElement type variable represent End Element.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean swipe(TestCases testCase, MobileElement startElement, MobileElement endElement) {

		CustomDriver driver = testCase.getMobileDriver();

		if (driver == null) {
		} else {
			
			if(startElement==null) return false;
			if(endElement==null) return false;
			
			StringBuilder message = new StringBuilder("Swipe : ");
			try {
				TouchAction actions = new TouchAction(driver);
				//actions = actions.press(startElement).moveTo(endElement).release().perform();
				actions = actions.press(element(startElement)).moveTo(element(endElement)).release().perform();
				
				
				if (actions == null) {
					message = message.append(", not able to perform swipe.");
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
					message = null;
				} else {
					message = message.append(", swipe press successful.");
					Keyword.ReportStep_Pass(testCase, message.toString());
					message = null;
					return true;
				}
			} catch (Exception e) {
				message = message.append(", not able to perform swipe. Reason - ");
				message = message.append(e.getMessage());
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
				message = null;
			}

		}

		return false;
	}

	/**
	 * longPress is use to perform long press.
	 * 
	 * It returns Boolean type value representing action success.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param element
	 *            MobileElement type variable represent element on which long
	 *            press to perform.
	 * 
	 * @param durationInMilliSecond
	 *            long type variable represent duration in milliseconds.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean longPress(TestCases testCase, WebElement element, long durationInMilliSecond) {

		CustomDriver driver = testCase.getMobileDriver();

		StringBuilder message = new StringBuilder("Long Press : ");
		message = message.append(element.toString());

		if (driver == null) {
		} else {
			if (element != null) {
				TouchAction actions = new TouchAction(driver);
				
				//actions = actions.longPress(element, getDuration(durationInMilliSecond)).release().perform();
				actions = actions.longPress(longPressOptions().withElement(element(element)).withDuration(Duration.ofMillis(durationInMilliSecond))).release().perform();
				
				if (actions == null) {
					message = message.append(", not able to perform long press.");
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
					message = null;
					return false;
				} else {
					message = message.append(", long press successful.");
					Keyword.ReportStep_Pass(testCase, message.toString());
					message = null;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * clickOnCoordinate is use to click on given coordinates
	 * 
	 * It returns Boolean type value representing action success.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param xCoordinate
	 *            int type variable represent x coordinate.
	 * 
	 * @param yCoordinate
	 *            int type variable represent y coordinate.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean clickOnCoordinate(TestCases testCase, int xCoordinate, int yCoordinate) {
		CustomDriver driver = testCase.getMobileDriver();
		StringBuilder message = new StringBuilder("Click On Coordinate : ");

		if (driver == null) {
		} else {
			try {
				TouchAction action = new TouchAction(driver);
				//action = action.tap(xCoordinate, yCoordinate).release().perform();
				action = action.tap(tapOptions().withPosition(point(xCoordinate, yCoordinate))).perform();

				if (action == null) {
					message = message.append(" Not able to click on coordinate - [");
					message = message.append(xCoordinate);
					message = message.append(",");
					message = message.append(yCoordinate);
					message = message.append("]");
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
					message = null;
				} else {
					message = message.append("Click successful");
					Keyword.ReportStep_Pass(testCase, message.toString());
					message = null;
					return true;
				}

			} catch (Exception e) {

				message = message.append(" Not able to click on coordinate - [");
				message = message.append(xCoordinate);
				message = message.append(",");
				message = message.append(yCoordinate);
				message = message.append("]. Reason - ");
				message = message.append(e.getMessage());
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, message.toString());
				message = null;
			}
		}

		return false;
	}

	/**
	 * getAttribute is use to get Attribute
	 * 
	 * It returns String type value representing Element attribute.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param objectDefinitions
	 *            HashMap String, MobileObject type variable represent object
	 *            definition collection.
	 * 
	 * @param locatorType
	 *            String type variable represents locator type.
	 * 
	 * @param locatorValue
	 *            String type variable represents locator value.
	 * 
	 * @param requiredAttribute
	 *            String type variable represents attribute name.
	 * @author E880579
	 * @return String
	 */

	public static String getAttribute(TestCases testCase, String locatorType, String locatorValue,
			String requiredAttribute) {
		MobileElement element = getMobElement(testCase, locatorType, locatorValue);
		String attribute = FrameworkGlobalVariables.BLANK;
		if (element != null) {
			attribute = element.getAttribute(requiredAttribute);
			return attribute == null ? "Attribute Not Present" : attribute;
		}

		return attribute;
	}
	
	/**
	 * getAttribute is use to get Attribute
	 * 
	 * It returns String type value representing Element attribute.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param element
	 *            MobileElement type variable represents object.
	 * 
	 * @param requiredAttribute
	 *            String type variable represents attribute name.
	 * @author E880579
	 * @return String
	 */

	public static String getAttribute(TestCases testCase, MobileElement element,
			String requiredAttribute) {
		String attribute = FrameworkGlobalVariables.BLANK;
		if (element != null) {
			attribute = element.getAttribute(requiredAttribute);
			return attribute == null ? "Attribute Not Present" : attribute;
		}

		return attribute;
	}

	/**
	 * getAttribute is use to get Attribute
	 * 
	 * It returns String type value representing Element attribute.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @param objectDefinitions
	 *            HashMap String, MobileObject type variable represent object
	 *            definition collection.
	 * 
	 * @param objectName
	 *            String type variable represents object name.
	 * 
	 * @param requiredAttribute
	 *            String type variable represents attribute name.
	 * @author E880579
	 * @return String
	 */

	public static String getAttribute(TestCases testCase, HashMap<String, MobileObject> objectDefinitions,
			String objectName, String requiredAttribute) {
		CustomDriver driver = testCase.getMobileDriver();
		String attribute = FrameworkGlobalVariables.BLANK;
		if (driver != null) {
			MobileElement element = getMobElement(objectDefinitions, testCase, objectName);
			if (element != null) {
				attribute = element.getAttribute(requiredAttribute);
				return attribute == null ? "Attribute Not Present" : attribute;
			}
		}

		return attribute;
	}

	public static MobileElement scrollTo(TestCases testCase, String label) {
		CustomDriver driver = testCase.getMobileDriver();

		if (driver != null) {
			if (isRunningOnAndroid(testCase)) {
				return scrollToAndroid(testCase, label);
			} else {
				return scrollToIOS(testCase, label);
			}
		} else {
			return null;
		}

	}

	/**
	 * launchSettingsAppOnIOS is use to launch Settings app on IOS.
	 * 
	 * It returns boolean type value representing true for Exists and false for
	 * not.
	 * 
	 * <p>
	 * Currently it works on Perfecto Only.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @author E880579
	 * @return boolean
	 */
	public static boolean launchSettingsAppOnIOS(TestCases testCase) {

		boolean flag = false;

		if (testCase.getTestCaseInputs().isRunningOn("Perfecto")) {
			CustomDriver driver = testCase.getMobileDriver();

			HashMap<String, String> settings = new HashMap<>();
			settings.put("name", "Settings");
			try {
				driver.executeScript("mobile:application:open", settings);
			} catch (Exception e) {
				FrameworkGlobalVariables.logger4J.logWarn("Launch Setting App: App is already open.");
			}

			flag = true;
		} else {
			FrameworkGlobalVariables.logger4J.logWarn("Launch Settings App On IOS is available on Perfecto Only.");
		}

		return flag;

	}

	/**
	 * closeSettingsAppOnIOS is use to close already opened IOS Settings app.
	 * 
	 * It returns boolean type value representing true for Exists and false for
	 * not.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean closeSettingsAppOnIOS(TestCases testCase) {

		boolean flag = false;

		if (testCase.getTestCaseInputs().isRunningOn("Perfecto")) {
			CustomDriver driver = testCase.getMobileDriver();

			HashMap<String, String> settings = new HashMap<>();
			settings.put("name", "Settings");

			try {
				driver.executeScript("mobile:application:close", settings);

			} catch (Exception e) {
				FrameworkGlobalVariables.logger4J.logWarn("Settings is already closed, continue with script");
			}
		} else {
			FrameworkGlobalVariables.logger4J.logWarn("Close Settings App On IOS is available on Perfecto Only.");
		}

		return flag;

	}

	/**
	 * getRecentNotificationMessageOnIOS is use to read Latest IOS push
	 * notification.
	 * 
	 * It returns ArrayList of String representing all the message received
	 * recently. It opens the message tray from the top of device and reads all
	 * the recent messages.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static ArrayList<String> getRecentNotificationMessageOnIOS(TestCases testCase) {

		ArrayList<String> messages = new ArrayList<>();

		switch (testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME).toUpperCase()) {
		case Mobile.IOS:
			CustomIOSDriver driver = (CustomIOSDriver) (testCase.getMobileDriver());

			if (driver == null) {
				return messages;
			}

			Dimension dimension = getDimensionOfScreen(testCase);

			swipe(testCase, 5, 5, 0, dimension.getHeight() / 2);

			String xpath = "//*[@label='Recent']/preceding-sibling::UIACollectionCell";

			List<WebElement> messagesObject = MobileUtils.getMobElements(testCase, "XPATH", xpath, true, false, true);

			Iterator<WebElement> iter = messagesObject.iterator();

			while (iter.hasNext()) {
				messages.add(iter.next().getAttribute("label"));
			}

			swipe(testCase, 5, dimension.getHeight() - 5, 5, dimension.getHeight() / -2);
			break;
		case Mobile.ANDROID:
			break;
		}

		return messages;
	}

	/**
	 * clearRecentNotification is use to clear all the Push notification
	 * received recently.
	 * 
	 * It doesn't return any value.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @author E880579
	 * 
	 */

	public static void clearRecentNotification(TestCases testCase) {

		String os = testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME);

		if (testCase.getMobileDriver() == null) {
			return;
		}

		switch (os.toUpperCase()) {
		case Mobile.IOS:
			Dimension dimension = MobileUtils.getDimensionOfScreen(testCase);

			MobileUtils.swipe(testCase, 5, 5, 0, dimension.getHeight() / 2);

			String xpath = "//*[@label='Recent']/following-sibling::UIAButton";
			MobileElement clear = MobileUtils.getMobElement(testCase, "XPATH", xpath, true, false);
			if (clear == null) {
				// Clear button doesn't Exists
			} else {
				try {
					clear.click();

					clear = MobileUtils.getMobElement(testCase, "XPATH", xpath, true, false);

					if (clear != null) {
						clear.click();
					}

					swipe(testCase, 5, dimension.getHeight() - 5, 5, dimension.getHeight() / -2);
				} catch (Exception e) {
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
							"Clear Recent Notification : " + FrameworkUtils.getMessage(e));
				}

			}
			break;
		case Mobile.ANDROID:
			clearNotificationAndroidOnly(testCase);
			break;
		}
	}

	/**
	 * isMobileObjectDefinitionExists is use to check whether Object Definition
	 * collection has Object by name or not.
	 * 
	 * It returns boolean type value representing true for Exists and false for
	 * not.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * @param objectDefinitions
	 *            &lt;HashMap,MobileObject&gt; type variable that represents
	 *            Mobile Object definition collection.
	 * @param objectName
	 *            String type variable that represents Object name in
	 *            collection.
	 * 
	 * @author E880579
	 * @return boolean
	 */
	public static boolean isMobileObjectDefinitionExists(TestCases testCase,
			HashMap<String, MobileObject> objectDefinitions, String objectName) {

		if (objectDefinitions.containsKey(objectName)) {
			return true;
		} else {
			Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Is Mobile Definition Available : Object Definition for - " + objectName + " doesn't exists.");
			return false;
		}
	}

	/**
	 * isRunningOnAndroid is use to check whether current Target phone is
	 * running on Android.
	 * 
	 * It returns boolean type value representing true for Android and false of
	 * others.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents Test Cases.
	 * 
	 * @author E880579
	 * @return boolean
	 */
	public static boolean isRunningOnAndroid(TestCases testCase) {
		TestCaseInputs inputs = testCase.getTestCaseInputs();
		return inputs.getInputValue(TestCaseInputs.OS_NAME).equalsIgnoreCase(Mobile.ANDROID);
	}

	/**
	 * <p>
	 * switchonElementAndroidOnly is use to switch Android Button.
	 * </p>
	 * <p>
	 * It returns boolean type value representing success/failure.
	 * </p>
	 * <p>
	 * Note: Method works on Android Platform only.
	 * </p>
	 * 
	 * @param objectDefinition
	 *            HashMap&lt;String, MobileObject&gt; type variable that
	 *            represents Mobile Element Locator collection.
	 * @param testCase
	 *            TestCases type variable that represents Test case.
	 * @param objName
	 *            String type variable that represents object name in
	 *            collection.
	 * @param switchON
	 *            boolean type variable that represents option on switch. true
	 *            represents turn on switch.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean switchonElementAndroidOnly(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, boolean switchON) {

		// Check for Mobile Driver, whether its null or not.
		if (testCase.getMobileDriver() == null) {
			return false;
		}

		boolean result = true;

		String checked = "checked";

		String messageHeader = "Set Switch [Android Only] : ";

		try {
			if (isRunningOnAndroid(testCase)) {

				// Get Mobile Element
				WebElement ele = MobileUtils.getMobElement(objectDefinition, testCase, objName);

				// Check element exist or not
				if (ele != null) {

					// Get current state of switch
					boolean currentState = Boolean.valueOf(ele.getAttribute(checked));

					// Turn ON Switch
					if (switchON) {
						if (currentState) {
							Keyword.ReportStep_Pass(testCase, messageHeader + objName + " is already ON");
						} else {
							MobileUtils.clickOnElement(objectDefinition, testCase, objName);
							Keyword.ReportStep_Pass(testCase, messageHeader + objName + " turned ON");
						}

					} else {
						// Turn OFF Switch
						if (currentState) {
							MobileUtils.clickOnElement(objectDefinition, testCase, objName);
							Thread.sleep(1000);
							Keyword.ReportStep_Pass(testCase, messageHeader + objName + " : turned OFF");
						} else {
							Keyword.ReportStep_Pass(testCase, messageHeader + objName + " : is already OFF");
						}
					}
				} else {
					Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
							messageHeader + objName + " Element doesnot exists");
					result = false;
				}

			} else {
				return false;
			}
		} catch (Exception e) {
			Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
					messageHeader + FrameworkUtils.getMessage(e));
			result = false;
		}
		return result;
	}

	/**
	 * <p>
	 * isMobElementExists checks whether Mobile Element exists or not.
	 * </p>
	 * <p>
	 * Method provides flexibility to provide Locator type and value in line.
	 * </p>
	 * 
	 * <p>
	 * Its return boolean type value representing success/failure.
	 * </p>
	 * 
	 * @param locatorType
	 *            String type variable that represents Locator type.
	 * 
	 * @param locatorVal
	 *            String type variable that represents Locator value.
	 * 
	 * @param testCase
	 *            TestCases type variable that represents test case.
	 * 
	 * @param timeOutInSeconds
	 *            Integer type variable that represents custom time out in
	 *            Seconds.
	 * 
	 * @param autoPopHandler
	 *            Optional boolean parameter representing flag to handle popups
	 *            if any is displayed. Default is true
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(String locatorType, String locatorVal, TestCases testCase,
			int timeOutInSeconds, boolean... autoPopHandler) {

		// Check for Mobile Driver, whether its null or not.
		if (testCase.getMobileDriver() == null) {
			return false;
		}

		// Create Mobile Object instance from custom locator provided
		MobileObject mObject = new MobileObject(locatorType, locatorVal);
		String objName = "[" + locatorType + ":" + locatorVal + "]";
		HashMap<String, MobileObject> objectDefinition = new HashMap<String, MobileObject>();
		objectDefinition.put(objName, mObject);

		// Call isMobElementExists method that takes MobileObject collection as
		// input.

		return isMobElementExists(objectDefinition, testCase, objName, timeOutInSeconds, autoPopHandler);
	}

	/**
	 * <p>
	 * isMobElementExists is used to check whether Mobile Element exists or not.
	 * </p>
	 * 
	 * <p>
	 * Its return boolean type value representing success/failure.
	 * </p>
	 * 
	 * @param objectDefinition
	 *            HashMap&lt;String, MobileObject&gt; type variable that
	 *            represents collection of Mobile Object Definition.
	 * @param testCase
	 *            TestCases type variable that represents test case.
	 * @param objectName
	 *            String type variable that represents Object Name in
	 *            collection.
	 * @param timeOutInSeconds
	 *            Integer type variable that represents custom time out in
	 *            Seconds.
	 * @param autoPopHandler
	 *            Optional boolean parameter representing flag to handle popups
	 *            if any is displayed. Default is true
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objectName, int timeOutInSeconds, boolean... autoPopHandler) {

		boolean flag = true;
		boolean handlePopups = true;

		if (autoPopHandler.length == 1) {
			handlePopups = autoPopHandler[0];
		}

		// Check for Mobile Driver, whether its null or not.
		if (testCase.getMobileDriver() == null) {
			return false;
		}

		// Check for Object definition present in collection or not.
		if (isMobileObjectDefinitionExists(testCase, objectDefinition, objectName)) {

			// Get Mobile Object representing the object from collection
			MobileObject objDesc = objectDefinition.get(objectName);

			FluentWait<CustomDriver> fWait = Mobile.instantiateFluentWait(timeOutInSeconds * 1000,
					FrameworkGlobalVariables.POLLING_WAIT, testCase.getMobileDriver());

			// Start timer for performance data
			long startTime = System.currentTimeMillis();
			try {

				By locator = objDesc.getLocator(testCase.doUseXCUITest());
				MobileElement element = null;

				// Check for Locator type - NAME. Use Accessibility ID locator
				// to identify object
				if (objDesc.getLocationType().equalsIgnoreCase(MobileObject.NAME)) {

					if (isRunningOnAndroid(testCase)) {

						// Find Element by Accessibility ID for Android
						element = fWait.until(new Function<WebDriver, MobileElement>() {
							@SuppressWarnings("unchecked")
							@Override
							public MobileElement apply(WebDriver driver) {
								return ((AndroidDriver<MobileElement>) driver)
										.findElementByAccessibilityId(objDesc.getLocationValue());
							}
						});
					} else {

						// Find Element by Accessibility ID for IOS
						element = fWait.until(new Function<WebDriver, MobileElement>() {
							@SuppressWarnings("unchecked")
							@Override
							public MobileElement apply(WebDriver driver) {

								MobileElement element = ((IOSDriver<MobileElement>) driver)
										.findElementByAccessibilityId(objDesc.getLocationValue());

								return (element.isDisplayed()) ? element : null;
							}
						});
					}

				} else {
					// Find Element by Locator provided in Mobile Object
					// collection.
					element = (MobileElement) fWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
				}

				// Check whether element is null. Null represents Element
				// doesn't exists.
				return (element == null) ? false : true;

			} catch (TimeoutException e) { // Timeout of object identification

				handlePopups = false;
				if (handlePopups) {
					// Handle OS Pop ups
					if (Mobile.handlePopups(testCase.getMobileDriver(),
							testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME))) {

						// Return identifying element.
						return isMobElementExists(objectDefinition, testCase, objectName, timeOutInSeconds);
					}
				}

				return false;
			} catch (UnreachableBrowserException e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Is Mobile Element Available : WebDriver Exception Occured - " + FrameworkUtils.getMessage(e));
				Assert.fail();
			} catch (WebDriverException e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Is Mobile Element Available : WebDriver Exception Occured - " + FrameworkUtils.getMessage(e));

				flag = false;
			} catch (Exception e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Is Mobile Element Available : Unexpected Error occured - " + FrameworkUtils.getMessage(e));

				flag = false;
			} finally {
				try {
					if (FrameworkGlobalVariables.ENABLE_PERFORMANCE_DATA) {

						System.out.println(" locator : " + objDesc.getLocator().toString() + " Time taken "
								+ (System.currentTimeMillis() - startTime) + " msecs.");

						Keyword.ReportStep_Pass(testCase,
								"Time Take to identify element using Locator : " + objDesc.getLocator().toString()
								+ ". Time taken : " + (System.currentTimeMillis() - startTime) + " msecs.");
					}
				} catch (Exception e) {
					System.out.println("UnExpected Error Occured while collecting performance data - "
							+ FrameworkUtils.getMessage(e));
				}

			}
		} else {
			flag = false;
		}
		return flag;

	}

	/**
	 * setValueInPicker is used to set the field value.
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objectDefinition
	 *            HashMap&lt;String, MobileObject&gt; type variable that holds
	 *            the Mobile object definitions.
	 * @param objectName
	 *            String type variable that holds name of the object to interact
	 *            with.
	 * @param valueToSet
	 *            String type variable that represents value to set on Picker.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean setValueInPicker(TestCases testCase, HashMap<String, MobileObject> objectDefinition,
			String objectName, String valueToSet) {

		try {

			// Internally Set Value in Picker uses setValueToElement method.
			return MobileUtils.setValueToElement(objectDefinition, testCase, objectName, valueToSet);
		} catch (Exception e) {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
					"Set Value In Picker : Error Occured - " + FrameworkUtils.getMessage(e));
			return false;
		}

	}

	/**
	 * setValueInPicker is used to set the field value.
	 * 
	 * This method provides flexibility of providing in line Object locators.
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param locatorType
	 *            String type variable that holds the Mobile locator type.
	 * @param locatorValue
	 *            String type variable that holds
	 * @param valueToSet
	 *            String type variable that represents value to set on Picker.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean setValueInPicker(TestCases testCase, String locatorType, String locatorValue,
			String valueToSet) {

		try {

			// Internally Set Value in Picker uses setValueToElement method.
			boolean flag = MobileUtils.setValueToElement(testCase, locatorType, locatorValue, valueToSet);

			// Perform Action on Android Platform.
			if (flag && isRunningOnAndroid(testCase)) {
				flag = flag && pressEnterButton(testCase);
			}
			return flag;
		} catch (Exception e) {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
					"Set Value In Picker : Error Occured - " + FrameworkUtils.getMessage(e));
			return false;
		}
	}

	/**
	 * Method is used to get the label of a field.
	 * 
	 * Its return String type value representing text read.
	 * 
	 * @param objectDefinition
	 *            HashMap type variable that holds the object definitions.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the Object name to be cleared.
	 * @author E880579
	 * @return String/returns ##ELEMENT_NOT_FOUND## : ObjectName is not found.
	 */

	public static String getFieldLabel(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName) {

		String label = "##ELEMENT_NOT_FOUND## : " + objName;

		// Get Object object reference
		WebElement element = MobileUtils.getMobElement(objectDefinition, testCase, objName);

		// If Object is null object doesn't exists.
		if (element == null) {
			// Element not Found.
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
					"Get Label for field - " + objName + ". Message - Element not found.");
		} else {
			try {

				String osName = testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME);

				switch (osName.toUpperCase()) {
				case Mobile.IOS:
					label = getFieldValue(testCase, element);
					break;
				default:
					label = "Label is not supported for OS - " + osName;
				}

			} catch (Exception e) {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
						"ERROR Occured while Getting Label for field - " + objName + ". Message - "
								+ FrameworkUtils.getMessage(e));
			}
		}

		return label;
	}

	@Deprecated
	public static boolean fwait(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String element,
			int timeout) {
		MobileObject mObject = objectDefinition.get(element);
		FluentWait<CustomDriver> fWait = new FluentWait<CustomDriver>(testCase.getMobileDriver());
		
		//fWait.withTimeout(timeout, TimeUnit.SECONDS);
		//fWait.pollingEvery(500, TimeUnit.MILLISECONDS);
		
		
		fWait.withTimeout(Duration.ofSeconds(timeout));
		fWait.pollingEvery(Duration.ofMillis(500));
		
		fWait.ignoring(WebDriverException.class);
		fWait.ignoring(SocketException.class);

		try {
			fWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(mObject.getLocator()));
		} catch (TimeoutException e) {
			return false;
		} catch (Exception e) {
			Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION, " fwait - " + e.getMessage());
			return false;
		}
		return true;

	}

	/**
	 * <p>
	 * isKeyboardVisible is used to check whether Keyword is displayed or not.
	 * </p>
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param driver
	 *            AppiumDriver&lt;MobileElement&gt; type variable that holds the
	 *            Mobile Driver.
	 * @param testCase
	 *            TestCases type variable that represents test case.
	 * @param message
	 *            String type variable represents custom message.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isKeyboardVisible(CustomDriver driver, String... message) {

		boolean kbVisible = false;

		if (driver != null) {
			try {
				driver.hideKeyboard();
				kbVisible = true;
			} catch (Exception e) {
				kbVisible = false;
			}
		}

		return kbVisible;
	}

	/**
	 * <p>
	 * isKeyboardVisible is used to check whether Keyword is displayed or not.
	 * </p>
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param driver
	 *            AppiumDriver&lt;MobileElement&gt; type variable that holds the
	 *            Mobile Driver.
	 * @param testCase
	 *            TestCases type variable that represents test case.
	 * @param message
	 *            String type variable represents custom message.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isKeyboardVisible(TestCases testCase, CustomDriver driver, String... message) {

		boolean kbVisible = false;

		if (driver != null) {
			try {
				driver.hideKeyboard();
				kbVisible = true;
			} catch (Exception e) {
				kbVisible = false;
			}
		}

		return kbVisible;
	}

	/**
	 * <p>
	 * isKeyboardVisible is used to check whether Keyword is displayed or not.
	 * </p>
	 * <p>
	 * Note : Works only on IOS devices
	 * </p>
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param driver
	 *            AppiumDriver&lt;MobileElement&gt; type variable that holds the
	 *            Mobile Driver.
	 * @param keyToPressOnKeyboard
	 *            String type variable represents Key to Press On Keyboard.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean hideKeyboardIOS(CustomDriver driver, String keyToPressOnKeyboard) {
		boolean flag = false;
		if (driver != null) {
			try {
				((CustomIOSDriver) driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, keyToPressOnKeyboard);
				flag = true;
			} catch (Exception e) {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * <p>
	 * getFieldValue is used to get field value.
	 * </p>
	 * Its return String type value representing field value.
	 * 
	 * @param testCase
	 *            TestCases type variable represents test case.
	 * @param element
	 *            WebElement type variable represents element to get value.
	 * @author E880579
	 * @return String
	 */

	public static String getFieldValue(TestCases testCase, WebElement element) {
		String value = FrameworkGlobalVariables.BLANK;
		if (element != null) {

			String osName = testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME);

			switch (osName.toUpperCase()) {
			case Mobile.ANDROID:
				value = element.getText();
				if (value == null) {
					value = element.getAttribute("value");
					if (value == null) {
						value = element.getAttribute("contentDesc");
						if (value == null) {
							value = element.getAttribute("contentDescription");
						}
					} else {
						if (value.isEmpty()) {
							value = element.getAttribute("contentDesc");
							if (value == null) {
								value = element.getAttribute("contentDescription");
							}
						}
					}

				} else {
					if (value.isEmpty()) {
						value = element.getAttribute("value");
						if (value == null) {
							value = element.getAttribute("contentDesc");
						} else {
							if (value.isEmpty()) {
								value = element.getAttribute("contentDesc");
								if (value == null) {
									value = element.getAttribute("contentDescription");
								}
							}
						}
					}
				}

				break;

			case Mobile.IOS:
				value = element.getAttribute("value");
				if (value == null) {
					value = element.getAttribute("label");
					if (value == null) {
						value = element.getAttribute("name");
					} else {
						if (value.isEmpty()) {
							value = element.getAttribute("name");
						}
					}

				} else {
					if (value.isEmpty()) {
						value = element.getAttribute("label");
						if (value == null) {
							value = element.getAttribute("name");
						} else {
							if (value.isEmpty()) {
								value = element.getAttribute("name");
							}
						}
					}
				}

				break;
			}
		} else {
			value = "##ELEMENT_IS_NULL##";
		}

		return value;
	}

	/**
	 * Method is used to find the mobile element is displayed or not.
	 * 
	 * Its return boolean type value representing the presence or absence of the
	 * object.
	 * 
	 * @param objectDefinition
	 *            HashMap&lt;String, MobileObject&gt; type variable that holds
	 *            all the object definition on screen.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the element name from the
	 *            object definition sheet.
	 * @param autoPopupHandler
	 *            Boolean parameter to represent flag to handle popup.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName) {
		return isMobElementExists(objectDefinition, testCase, objName, true);
	}

	/**
	 * Method is used to find the mobile element is displayed or not.
	 * 
	 * Its return boolean type value representing the presence or absence of the
	 * object.
	 * 
	 * @param objectDefinition
	 *            HashMap&lt;String, MobileObject&gt; type variable that holds
	 *            all the object definition on screen.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the element name from the
	 *            object definition sheet.
	 * @param autoPopupHandler
	 *            Boolean parameter to represent flag to handle popup.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, boolean autoPopupHandler) {

		boolean flag = true;
		boolean handlePopups = true;

		if (testCase.getMobileDriver() == null) {
			return false;
		}

		if (objectDefinition.containsKey(objName)) {
			MobileObject mObject = objectDefinition.get(objName);
			FluentWait<CustomDriver> fWait = new FluentWait<CustomDriver>(testCase.getMobileDriver());
			
			//fWait.withTimeout(FrameworkGlobalVariables.LONG_WAIT, TimeUnit.MILLISECONDS);
			//fWait.pollingEvery(FrameworkGlobalVariables.POLLING_WAIT, TimeUnit.MILLISECONDS);
			
			fWait.withTimeout(Duration.ofMillis(FrameworkGlobalVariables.LONG_WAIT));
			fWait.pollingEvery(Duration.ofMillis(FrameworkGlobalVariables.POLLING_WAIT));
			
			
			fWait.ignoring(NoSuchElementException.class);
			fWait.ignoring(WebDriverException.class);
			fWait.ignoring(SocketException.class);

			long startTime = System.currentTimeMillis();

			try {

				MobileObject objDesc = objectDefinition.get(objName);

				MobileElement element;

				if (objDesc.getLocationType().equalsIgnoreCase(MobileObject.NAME)) {
					if (testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME)
							.equalsIgnoreCase(Mobile.IOS)) {

						element = fWait.until(new Function<WebDriver, MobileElement>() {
							@SuppressWarnings("unchecked")
							@Override
							public MobileElement apply(WebDriver driver) {
								MobileElement element = ((IOSDriver<MobileElement>) driver)
										.findElementByAccessibilityId(objDesc.getLocationValue());
								if (element != null) {
									return (element.isDisplayed()) ? element : null;
								} else {
									return null;
								}

							}
						});
					} else {
						element = fWait.until(new Function<WebDriver, MobileElement>() {
							@SuppressWarnings("unchecked")
							@Override
							public MobileElement apply(WebDriver driver) {
								return ((AndroidDriver<MobileElement>) driver)
										.findElementByAccessibilityId(objDesc.getLocationValue());
							}
						});
					}

				} else {
					By locator = objDesc.getLocator(testCase.doUseXCUITest());
					if (isRunningOnAndroid(testCase)) {
						element = (MobileElement) fWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
					} else {
						List<WebElement> elements = fWait
								.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
						element = null;
						for (int index = 0; index < elements.size(); ++index) {
							if (elements.get(index).isDisplayed()) {
								element = (MobileElement) elements.get(index);
								break;
							}
						}
					}
				}

				if (element != null) {
					return true;
				} else {
					return false;
				}

			} catch (TimeoutException e) {

				if (handlePopups) {
					if (Mobile.handlePopups(testCase.getMobileDriver(),
							testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME))) {
						return isMobElementExists(objectDefinition, testCase, objName);
					}
				}

				return false;
			} catch (UnreachableBrowserException e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Is Mobile Element Available : WebDriver Exception Occured - " + FrameworkUtils.getMessage(e));
				Assert.fail();
			} catch (WebDriverException e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Is Mobile Element Available : WebDriver Exception Occured - " + FrameworkUtils.getMessage(e));

				flag = false;
			} catch (Exception e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
						"Is Mobile Element Available : Unexpected Error occured - " + FrameworkUtils.getMessage(e));

				flag = false;
			} finally {
				if (FrameworkGlobalVariables.ENABLE_PERFORMANCE_DATA) {
					try {
						System.out.println(" locator : " + mObject.getLocator().toString() + " Time taken "
								+ (System.currentTimeMillis() - startTime) + " msecs.");

						Keyword.ReportStep_Pass(testCase,
								"Time Take to identify element using Locator : " + mObject.getLocator().toString()
								+ ". Time taken : " + (System.currentTimeMillis() - startTime) + " msecs.");
					} catch (Exception e) {
						System.out.println("UnExpected Error Occured while collecting performance data - "
								+ e.getLocalizedMessage().split("Command duration")[0]);
					}

				}

			}
		} else {
			Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Is Mobile Element Available : Object Definition for - " + objName + " doesn't exists.");
			flag = false;
		}
		return flag;

	}

	/**
	 * Method is used to find the mobile element is displayed or not.
	 * 
	 * Its return boolean type value representing the presence or absence of the
	 * object.
	 * 
	 * @param objectDefinition
	 *            HashMap&lt;String, MobileObject&gt; type variable that holds
	 *            all the object definition on screen.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the element name from the
	 *            object definition sheet.
	 * @param message
	 *            String type variable that represents Custom message.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, String message) {

		boolean flag = true;

		if (testCase.getMobileDriver() == null) {
			return false;
		}

		if (objectDefinition.containsKey(objName)) {
			FluentWait<CustomDriver> fWait = new FluentWait<CustomDriver>(testCase.getMobileDriver());
		
			//fWait.withTimeout(10, TimeUnit.SECONDS);
			//fWait.pollingEvery(500, TimeUnit.MILLISECONDS);
			

			fWait.withTimeout(Duration.ofSeconds(10));
			fWait.pollingEvery(Duration.ofMillis(500));
			
			
			fWait.ignoring(WebDriverException.class);
			fWait.ignoring(NoSuchElementException.class);
			fWait.ignoring(SocketException.class);
			try {
				MobileObject objDesc = objectDefinition.get(objName);

				By locator = objDesc.getLocator(testCase.doUseXCUITest());

				MobileElement element;

				if (objDesc.getLocationType().equalsIgnoreCase(MobileObject.NAME)) {
					if (testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME)
							.equalsIgnoreCase(Mobile.IOS)) {
						element = fWait.until(new Function<WebDriver, MobileElement>() {
							@SuppressWarnings("unchecked")
							@Override
							public MobileElement apply(WebDriver driver) {
								MobileElement element = ((IOSDriver<MobileElement>) driver)
										.findElementByAccessibilityId(objDesc.getLocationValue());

								if (element != null) {
									return (element.isDisplayed()) ? element : null;
								} else {
									return null;
								}
							}
						});
					} else {
						element = fWait.until(new Function<WebDriver, MobileElement>() {
							@SuppressWarnings("unchecked")
							@Override
							public MobileElement apply(WebDriver driver) {
								return ((AndroidDriver<MobileElement>) driver)
										.findElementByAccessibilityId(objDesc.getLocationValue());
							}
						});
					}

				} else {

					if (isRunningOnAndroid(testCase)) {
						element = (MobileElement) fWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
					} else {
						List<WebElement> elements = fWait
								.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
						element = null;
						for (int index = 0; index < elements.size(); ++index) {
							if (elements.get(index).isDisplayed()) {
								element = (MobileElement) elements.get(index);
								break;
							}
						}
					}
				}

				if (element != null) {
					return true;
				} else {

					return false;
				}
			} catch (TimeoutException e) {
				if (Mobile.handlePopups(testCase.getMobileDriver(),
						testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME))) {
					return isMobElementExists(objectDefinition, testCase, objName);
				}
				return false;
			} catch (Exception e) {
				Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION, message
						+ " Is Mobile Element Available : Unexpected Error occured - " + FrameworkUtils.getMessage(e));
				flag = false;
			}
		} else {
			Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
					message + " Is Mobile Element Available : Object Definition for - " + objName + " doesn't exists.");
			flag = false;
		}

		return flag;
	}

	/**
	 * Method is used to find the mobile element is displayed or not.
	 * 
	 * Its return boolean type value representing the presence or absence of the
	 * object.
	 * 
	 * This method considers
	 * 
	 * @param locatorType
	 *            String type variable that holds the object locator strategy
	 *            type.
	 * @param locatorVal
	 *            String type variable that holds the object locator strategy
	 *            value.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * 
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(String locatorType, String locatorVal, TestCases testCase) {

		if (testCase.getMobileDriver() == null) {
			return false;
		}

		MobileObject mObject = new MobileObject(locatorType, locatorVal);
		StringBuilder objName = new StringBuilder("[");
		objName = objName.append(locatorType);
		objName = objName.append(":");
		objName = objName.append(locatorVal);
		objName = objName.append("]");
		HashMap<String, MobileObject> objectDefinition = new HashMap<String, MobileObject>();
		objectDefinition.put(objName.toString(), mObject);
		return isMobElementExists(objectDefinition, testCase, objName.toString(), true);

	}

	/**
	 * Method is used to find the mobile element is displayed or not.
	 * 
	 * Its return boolean type value representing the presence or absence of the
	 * object.
	 * 
	 * @param locatorType
	 *            String type variable that holds the object locator strategy
	 *            type.
	 * @param locatorVal
	 *            String type variable that holds the object locator strategy
	 *            value.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * 
	 * @param autoHandlePopups
	 *            Optional boolean variable to handle popups.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean isMobElementExists(String locatorType, String locatorVal, TestCases testCase,
			boolean autoHandlePopups) {

		if (testCase.getMobileDriver() == null) {
			return false;
		}

		MobileObject mObject = new MobileObject(locatorType, locatorVal);
		StringBuilder objName = new StringBuilder("[");
		objName = objName.append(locatorType);
		objName = objName.append(":");
		objName = objName.append(locatorVal);
		objName = objName.append("]");
		HashMap<String, MobileObject> objectDefinition = new HashMap<String, MobileObject>();
		objectDefinition.put(objName.toString(), mObject);
		return isMobElementExists(objectDefinition, testCase, objName.toString(), autoHandlePopups);

	}

	/**
	 * Method is used to clear the Notifications on Android platform only.
	 * 
	 * Its return boolean type value representing the success or failure of
	 * action.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param message
	 *            Optional String type variable that represents Custom message.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean clearNotificationAndroidOnly(TestCases testCase, String... message) {

		boolean flag = true;
		CustomDriver driver = testCase.getMobileDriver();

		if (isRunningOnAndroid(testCase)) {

		} else {
			return false;
		}

		if (driver != null) {
			if (MobileUtils.showNotificationAndroidOnly(driver)) {

				try {
					driver.findElement(By.id("com.android.systemui:id/clear_all_button")).click();
				} catch (Exception e) {
					try {
						driver.findElement(By.id("com.android.systemui:id/dismiss_text")).click();
					} catch (Exception e1) {
						MobileUtils.pressBackButton(testCase);
					}

				}

			} else {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
						"Clear Notification : Not able to open the notification panel.");
				flag = false;
			}
		} else {
			flag = false;
		}

		if (message.length > 0) {
			StringBuilder stepMess = new StringBuilder(message[0]);
			if (flag) {
				stepMess = stepMess.append(" Clear Notification : Cleared the notification");
				Keyword.ReportStep_Pass(testCase, stepMess.toString());
			} else {
				stepMess = stepMess.append(" Clear Notification : Unable to clear the notification");
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMess.toString());
			}
		}

		return flag;
	}

	/**
	 * Method is used to turn on or off the air plane mode both on IOS [Device
	 * only] and Android [Both].
	 * 
	 * Its return boolean type value representing the success or failure of
	 * action.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param status
	 *            Boolean type variable representing the status to set, ON :
	 *            True and OFF : False.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean setAirPlaneMode(TestCases testCase, boolean status) {
		boolean flag = true;
		CustomDriver driver = testCase.getMobileDriver();

		TestCaseInputs inputs = testCase.getTestCaseInputs();

		if (driver == null) {
			return false;
		}

		switch (inputs.getInputValue(TestCaseInputs.OS_NAME).toUpperCase()) {
		case Mobile.ANDROID:
//			try {
//
//				Connection settings = status ? Connection.AIRPLANE : Connection.ALL; // new
//				// NetworkConnectionSetting(status
//				// ?
//				// 1
//				// :
//				// 6);
//
//				switch (inputs.getInputValue(TestCaseInputs.EXEC_LOCATION).toUpperCase()) {
//				case "PERFECTO":
//				case "PERFECTO_PRIVATE":
//				case "PERFECTO_PUBLIC":
//					Map<String, Object> pars = new HashMap<>();
//					pars.put("airplanemode", status?"enabled":"disabled");
//					((CustomAndroidDriver) driver).executeScript("mobile:network.settings:set", pars); 
//					break;
//				case "SAUCELABS":
//				case "SAUCELAB":
//					((CustomAndroidDriver) driver).setConnection(settings);
//					break;
//				default:
//					((CustomAndroidDriver) driver).setConnection(settings);
//					break;
//				}
//
//			} catch (Exception e) {
//				if (testCase.getTestCaseInputs().isRunningOn("Perfecto")) {
//					FrameworkGlobalVariables.logger4J
//					.logError("Set Airplane Mode : Ignoring error as suggested by Perfecto.");
//				} else {
//					StringBuilder stepMess = new StringBuilder(
//							"Set Airplane Mode: Error Occured during setting the location - ");
//					stepMess = stepMess.append(FrameworkUtils.getMessage(e));
//					Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FUNCTIONAL_FAILURE,
//							stepMess.toString());
//					flag = false;
//				}
//
//			}
			break;
		case Mobile.IOS:

			Dimension dimension = driver.manage().window().getSize();

			int height = dimension.getHeight();

			HashMap<String, Integer> coordinates = new HashMap<String, Integer>();

			coordinates.put(Mobile.START_X, 1);
			coordinates.put(Mobile.START_Y, height);

			coordinates.put(Mobile.END_X, 1);
			coordinates.put(Mobile.END_Y, (height / 2) * -1);

			MobileUtils.doSwipe(coordinates, testCase);

			TouchAction tAction = new TouchAction(driver);

			if (testCase.getTestCaseInputs().isRunningOn("Perfecto")) {

				if (MobileUtils.isMobElementExists("XPATH", "//*[@label='Airplane Mode']", testCase, 10)) {
					MobileUtils.clickOnElement(testCase, "XPATH", "//*[@label='Airplane Mode']");
					//tAction.tap(20, 20).perform();
					tAction.tap(tapOptions().withPosition(point(20, 20))).perform();
				} else {

					if (MobileUtils.isMobElementExists("XPATH", "//*[@label='Continue']", testCase, 10)) {
						MobileUtils.clickOnElement(testCase, "XPATH", "//*[@label='Continue']");
						MobileUtils.clickOnElement(testCase, "XPATH", "//*[@label='Airplane Mode']");
						//tAction.tap(20, 20).perform();
						tAction.tap(tapOptions().withPosition(point(20, 20))).perform();
					} else {
						flag = false;
					}
				}

			} else {

				switch (testCase.getPlatform()) {
				case Mobile.IOS_MEDIUM:

					try {
						Thread.sleep(3000);
						//tAction.press(43, 195).release().perform();
						tAction.press(point(43, 195)).release().perform();
					} catch (Exception e) {

						flag = false;
					}

					break;
				case Mobile.IOS_LARGE:
					try {
						Thread.sleep(3000);
						//tAction.press(43, 195).release().perform();
						tAction.press(point(43, 195)).release().perform();
					} catch (Exception e) {
						flag = false;
					}
					break;
				case Mobile.IOS_Extra_LARGE:
					break;
				}

				coordinates.put(Mobile.START_X, 1);
				coordinates.put(Mobile.START_Y, 1);

				coordinates.put(Mobile.END_X, 1);
				coordinates.put(Mobile.END_Y, (height / 2));

				MobileUtils.doSwipe(coordinates, testCase);

				if (status) {
					if (MobileUtils.getMobElement(testCase, MobileObject.NAME, "Airplane mode on") == null) {
						flag = false;
					}
				} else {
					flag = true;
				}
				break;
			}
		}

		return flag;
	}

	/**
	 * Method is used to generate new Email address for a given email domain.
	 * 
	 * Its return String type value representing New Email address.
	 * 
	 * @param emailDomain
	 *            String type variable that holds the email Domain.
	 * 
	 * @author E880579
	 * @return String
	 */

	public static String getEmailID(String emailDomain) {

		return "Honeywell." + System.currentTimeMillis()
		+ (emailDomain.contains("@") ? emailDomain : "@" + emailDomain);
	}

	/**
	 * <p>
	 * Method is used to do a back button press operation.
	 * </p>
	 * <p>
	 * Note : For Android Platform only.
	 * </p>
	 * 
	 * Its return boolean type value representing Success/Failure.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param message
	 *            Optional String type variable that represents Custom message.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean pressBackButton(TestCases testCase, String... message) {

		StringBuilder stepMess = new StringBuilder(message.length == 0 ? FrameworkGlobalVariables.BLANK : message[0]);

		CustomDriver driver = testCase.getMobileDriver();
		if (driver != null) {
			try {
				((CustomAndroidDriver) driver).pressKeyCode(4);
				
				stepMess.append(" navigate back successfully");
				Keyword.ReportStep_Pass(testCase, stepMess.toString());
				return true;
			} catch (Exception e) {
				stepMess.append(" Navigating back failed. Error message : ");
				stepMess.append(FrameworkUtils.getMessage(e));
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMess.toString());
				return false;
			}
		} else {
			stepMess.append(" Navigating back failed.");
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMess.toString());
			return false;
		}
	}

	/**
	 * <p>
	 * Method is used to do a Enter button press operation.
	 * </p>
	 * <p>
	 * Note : For Android Platform only.
	 * </p>
	 * 
	 * Its return boolean type value representing Success/Failure.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean pressEnterButton(TestCases testCase) {
		CustomDriver driver = testCase.getMobileDriver();

		if (isRunningOnAndroid(testCase)) {
			if (driver != null) {
				try {
					((CustomAndroidDriver) driver).pressKeyCode(66);
					return true;
				} catch (Exception e) {
					StringBuilder stepMess = new StringBuilder("ERROR occured while doing enter key press - ");
					stepMess = stepMess.append(FrameworkUtils.getMessage(e));
					FrameworkGlobalVariables.logger4J.logError(stepMess.toString());
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Method is used to clear the pre-populated text.
	 * 
	 * Its return String type value representing success/failure.
	 * 
	 * @param objectDefinition
	 *            HashMap type variable that holds the object definitions.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the Object name to be cleared.
	 * 
	 * @param message
	 *            Optional String type variable that holds custom message.
	 * 
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean clearPasswordField(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, String... message) {

		boolean flag = true;

		WebElement element = MobileUtils.getMobElement(objectDefinition, testCase, objName);

		StringBuilder methodMsg = new StringBuilder(message.length == 0 ? FrameworkGlobalVariables.BLANK : message[0]);

		if (element != null) {
			try {
				element.clear();
			} catch (Exception e) {

				methodMsg = methodMsg.append(" Clear Object - '");
				methodMsg = methodMsg.append(objName);
				methodMsg = methodMsg.append("': Error message :");
				methodMsg = methodMsg.append(FrameworkUtils.getMessage(e));

				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, methodMsg.toString());

				flag = false;
			}
		} else {
			flag = false;
		}

		if (flag) {
			methodMsg.append(" cleared the password field");
			Keyword.ReportStep_Pass(testCase, methodMsg.toString());
		} else {
			methodMsg.append(" Unable to clear the password field");
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, methodMsg.toString());
		}

		return flag;
	}

	/**
	 * Method is used to get the text present in a field.
	 * 
	 * Its return String type value representing text read.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param locatorType
	 *            String type variable that holds the locator type.
	 * @param locatorValue
	 *            String type variable that holds the locator value.
	 * @author E880579
	 * @return String/returns ##ELEMENT_NOT_FOUND## : ObjectName is not found.
	 */

	public static String getFieldValue(TestCases testCase, String locatorType, String locatorValue) {

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		MobileObject object = new MobileObject(locatorType, locatorValue);

		HashMap<String, MobileObject> objectDefinition = new HashMap<String, MobileObject>();

		objectDefinition.put(objectName.toString(), object);

		return MobileUtils.getFieldValue(objectDefinition, testCase, objectName.toString());

	}

	/**
	 * Method is used to get the text present in a field.
	 * 
	 * Its return String type value representing text read.
	 * 
	 * @param objectDefinition
	 *            HashMap type variable that holds the object definitions.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the Object name to be cleared.
	 * @author E880579
	 * 
	 * @return String/returns ##ELEMENT_NOT_FOUND## : ObjectName is not found.
	 */

	public static String getFieldValue(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName) {

		String value = "##ELEMENT_NOT_FOUND## : " + objName;

		WebElement element = MobileUtils.getMobElement(objectDefinition, testCase, objName);

		if (element != null) {
			try {
				String osName = testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME);

				switch (osName.toUpperCase()) {
				case Mobile.ANDROID:
					value = element.getText();
					if (value.isEmpty()) {
						value = element.getAttribute("value");
						if (value.isEmpty()) {
							value = element.getAttribute("contentDesc");
						}
					}
					break;

				case Mobile.IOS:
					value = element.getAttribute("value");
					if (value.isEmpty()) {
						value = element.getAttribute("label");
						if (value.isEmpty()) {
							value = element.getAttribute("name");
						}
					}
					break;
				}

			} catch (Exception e) {

				StringBuilder message = new StringBuilder("ERROR Occured while Getting Value for field - ");
				message = message.append(objName);
				message = message.append(". Message - ");
				message = message.append(FrameworkUtils.getMessage(e));
				value = FrameworkGlobalVariables.BLANK;
			}
		}

		return value;
	}

	/**
	 * Method is used to clear the pre-populated text.
	 * 
	 * Its return String type value representing success/failure.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param locatorType
	 *            String type variable that holds the locator type.
	 * @param locatorValue
	 *            String type variable that holds the locator value.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean clearTextField(TestCases testCase, String locatorType, String locatorValue) {

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		MobileObject object = new MobileObject(locatorType, locatorValue);

		HashMap<String, MobileObject> objectDefinition = new HashMap<String, MobileObject>();

		objectDefinition.put(objectName.toString(), object);

		return MobileUtils.clickOnElement(objectDefinition, testCase, objectName.toString());
	}

	/**
	 * Method is used to clear the pre-populated text.
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param objectDefinition
	 *            HashMap type variable that holds the object definitions.
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param objName
	 *            String type variable that holds the Object name to be cleared.
	 * @param message
	 *            Optional String type variable that holds custom message.
	 * 
	 * @author E880579
	 * @return boolean
	 */

	public static boolean clearTextField(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName,
			String... message) {

		boolean flag = true;

		StringBuilder stepMess = new StringBuilder(message.length == 0 ? FrameworkGlobalVariables.BLANK : message[0]);

		WebElement element = MobileUtils.getMobElement(objectDefinition, testCase, objName);

		if (element != null) {
			try {
				element.clear();
			} catch (Exception e) {
			}
		}

		String newValue = MobileUtils.getFieldValue(objectDefinition, testCase, objName);

		if (FrameworkGlobalVariables.BLANK.equals(newValue.replace("Editing.", FrameworkGlobalVariables.BLANK))) {
			return true;
		} else {

			if (testCase.getPlatform().contains(Mobile.IOS)) {
				return true;
			} else {
				String preText = MobileUtils.getFieldValue(objectDefinition, testCase, objName);

				if (!preText.equalsIgnoreCase("##ELEMENT_NOT_FOUND## : " + objName)) {

					element = MobileUtils.getMobElement(objectDefinition, testCase, objName);

					if (element != null) {

						try {
							Point point = element.getLocation();

							TouchAction tAction = new TouchAction(testCase.getMobileDriver());

							//tAction.tap(point.getX(), point.getY()).perform();
							tAction.tap(tapOptions().withPosition(point(point.getX(), point.getY()))).perform();

							for (int i = preText.length(); i > 0; i--) {
								((CustomAndroidDriver) testCase.getMobileDriver()).pressKeyCode(22);
							}

							for (int i = preText.length(); i > 0; i--) {
								((CustomAndroidDriver) testCase.getMobileDriver()).pressKeyCode(67);
							}
						} catch (Exception e) {
							stepMess = stepMess.append("ERROR occured while clearing field - ");
							stepMess = stepMess.append(objName);
							stepMess = stepMess.append(". Message - ");
							stepMess = stepMess.append(FrameworkUtils.getMessage(e));
							FrameworkGlobalVariables.logger4J.logError(stepMess.toString());
							flag = false;
						}

					} else {
						flag = false;
					}

				} else {
					flag = false;
				}
			} // End of Else block

		} // End of Else block

		if (flag) {
			stepMess = stepMess.append(" Successfully clear the field.");
			Keyword.ReportStep_Pass(testCase, stepMess.toString());
		} else {
			stepMess = stepMess.append(" Unable to clear the field.");
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMess.toString());
		}

		return flag;
	}

	/**
	 * Method is used to select the browser from the open with pop up.
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the test case related data.
	 * @param browserName
	 *            String type variable that holds the browser name to be
	 *            selected.
	 * @author E880579
	 * @return boolean
	 */

	public static boolean selectBrowser(TestCases testCase, String browserName) {
		CustomDriver driver = testCase.getMobileDriver();
		boolean flag = true;
		if (driver != null) {
			try {
				List<WebElement> browsers = MobileUtils.getMobElements(testCase, "ID", "android:id/text1");

				int index = 0;
				WebElement browserOption;

				while (index < browsers.size()) {
					browserOption = browsers.get(index);
					if (browserOption.getText().equalsIgnoreCase(browserName)) {
						if (browserOption.isEnabled()) {
							browserOption.click();
							flag = MobileUtils.clickOnElement(testCase, "ID", "android:id/button_once") ? flag : false;
							break;
						}
					} else {
						// Check for the next option available
					}

					++index;
				}

				if (index == browsers.size()) {
					flag = false;
				} else {
					// Nothing to do..
				}
			} catch (Exception e) {
				flag = false;
			}

		} else {
			flag = false;
		}

		return flag;
	}

	// TODO : Remove in next release
	/**
	 * Method is used to do Double Tap on an element.
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param locatorType
	 *            String type variable that holds the locator type.
	 * @param locatorValue
	 *            String type variable that holds the locator value.
	 * @author E880579
	 * @return boolean
	 */
	@Deprecated
	public static boolean doDoubleTap(TestCases testCase, String locatorType, String locatorValue) {

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> object = new HashMap<String, MobileObject>();
		object.put(objectName.toString(), mObject);
		return doDoubleTap(object, testCase, objectName.toString());
	}

	// TODO : Remove in next release
	/**
	 * Method is used to do Double Tap on an element.
	 * 
	 * Its return boolean type value representing success/failure.
	 * 
	 * @param objectDefinition
	 *            HashMap type variable that holds the Object definitions.
	 * @param testCase
	 *            TestCases type variable that holds the TestCase related
	 *            information.
	 * @param objName
	 *            String type variable that represent Object name.
	 * @author E880579
	 * @return boolean
	 */

	@Deprecated
	public static boolean doDoubleTap(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName) {
		boolean flag = true;

		WebElement element = MobileUtils.getMobElement(objectDefinition, testCase, objName);

		if (element == null) {
			flag = false;
		} else {
			try {
				Point point = element.getLocation();
				CustomDriver driver = testCase.getMobileDriver();
				TouchAction tAction = new TouchAction(driver);

				if (isRunningOnAndroid(testCase)) {

					Instant start = Instant.now();
					Instant end = Instant.now().plusMillis(100);
					Duration duration = Duration.between(start, end);

					//tAction.tap(point.getX(), point.getY()).waitAction(duration).tap(0, 0).perform();
					tAction.tap(tapOptions().withPosition(point(point.getX(), point.getY()))).waitAction().tap(tapOptions().withPosition(point(0,0))).perform();
				
					// mtouch.add(tAction).perform();
				} else {
					//TouchAction action0 = new TouchAction(driver).tap(element);
					TouchAction action0 = new TouchAction(driver).tap(tapOptions().withElement(element(element)));
					
					//TouchAction action1 = new TouchAction(driver).tap(element);
					TouchAction action1 = new TouchAction(driver).tap(tapOptions().withElement(element(element)));
					action0.perform();
					action1.perform();

					// clickOnElement(objectDefinition, testCase, objName);
					// clickOnElement(objectDefinition, testCase, objName);
					// tAction.press(point.getX(),
					// point.getY()).perform().release().press(0, 0).perform();
				}

			} catch (Exception e) {
				flag = false;
			}
		}

		return flag;
	}

	/**
	 * Hides Keyboard, if displayed. The driver argument is AppiumDriver type
	 * variable that holds Appium driver representing current session.
	 * <p>
	 * Its return type is void.
	 * 
	 * @param driver
	 *            AppiumDriver type variable that holds the current session with
	 *            the application.
	 * @param message
	 *            String type variable that represents Custom message.
	 * 
	 * @author E880579
	 * 
	 */

	public static void hideKeyboard(CustomDriver driver, String... message) {

		StringBuilder methodMsg = new StringBuilder(message.length == 0 ? FrameworkGlobalVariables.BLANK : message[0]);
		if (driver != null) {
			try {
				driver.hideKeyboard();
				methodMsg = methodMsg.append(" hide the keyboard from screen.");
			} catch (Exception e) {
				methodMsg = methodMsg.append(" Unable to hide the keyboard.Error message :");
				methodMsg = methodMsg.append(FrameworkUtils.getMessage(e));
				FrameworkGlobalVariables.logger4J.logError(methodMsg.toString());
			}
		}

		methodMsg = null;
	}

	/**
	 * Returns an Boolean value specifying Success or failure of displaying
	 * Notification on Android application only. The driver argument is
	 * AppiumDriver type variable that holds Appium driver representing current
	 * session.
	 * 
	 * This method returns a boolean value specifying the success/failure of
	 * displaying Notification on Android application only.
	 * 
	 * @param driver
	 *            AppiumDriver type variable that holds the current session with
	 *            the application.
	 * @author E880579
	 * @return Success/Failure
	 */

	public static boolean showNotificationAndroidOnly(CustomDriver driver) {

		boolean flag = true;

		if (driver != null) {
			try {
				((CustomAndroidDriver) driver).openNotifications();
				Thread.sleep(10000);
			} catch (Exception e) {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * Returns an Boolean value specifying Success or failure of Swipe
	 * Operation. The testCase argument is TestCases type variable that holds
	 * the values that has to pass amount method like Webdriver instance. The
	 * coordinates argument is a HashMap that contains the object X and Y Start
	 * and End coordinates.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Swipe Operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param coordinates
	 *            HashMap that contains the coordinates. The key it accepts
	 *            Mobile.START_X,Mobile.START_Y,Mobile.END_X,Mobile.END_Y
	 * @author E880579
	 * @return True/False
	 */

	public static boolean doSwipe(HashMap<String, Integer> coordinates, TestCases testCase) {

		CustomDriver driver = testCase.getMobileDriver();
		boolean flag = true;

		if (driver != null) {
			TouchAction actions = new TouchAction(driver);
			try {
				
				//actions.press(coordinates.get(Mobile.START_X), coordinates.get(Mobile.START_Y)).moveTo(coordinates.get(Mobile.END_X), coordinates.get(Mobile.END_Y)).release().perform();
			
				actions.press(point(coordinates.get(Mobile.START_X), coordinates.get(Mobile.START_Y))).moveTo(point(coordinates.get(Mobile.END_X), coordinates.get(Mobile.END_Y))).release().perform();
				
			} catch (Exception e) {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
						"Do Swipe : Error occured, while clicking on coordinate - " + coordinates + ". Message - "
								+ FrameworkUtils.getMessage(e));
				flag = false;
			}

		} else {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, "Do Swipe : Driver can't be null.");
			flag = false;
		}

		return flag;

	}

	/**
	 * Returns a List of Mobile elements, Based on the Object property in
	 * objectDefinition HashMap. The testCase argument is TestCases type variable
	 * that holds the values that has to pass amount method like Webdriver
	 * instance. The objectDefinition argument is a HashMap that contains the object
	 * definition read from the JSON files. The objName argument is the Key in
	 * the HashMap objectDefinition.
	 * 
	 * This method considers default behavior - handles pop, reports steps and
	 * returns visible objects only.
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * 
	 * 
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(TestCases testCase, String locatorType, String locatorValue) {

		StringBuilder objName = new StringBuilder(locatorType);
		objName = objName.append(":");
		objName = objName.append(locatorValue);
		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();
		objects.put(objName.toString(), mObject);
		return MobileUtils.getMobElements(objects, testCase, objName.toString(), true, true, false);
	}

	/**
	 * Returns a List of Mobile elements, Based on the Object property in
	 * objectDefinition HashMap. The testCase argument is TestCases type variable
	 * that holds the values that has to pass amount method like Webdriver
	 * instance. The objectDefinition argument is a HashMap that contains the object
	 * definition read from the JSON files. The objName argument is the Key in
	 * the HashMap objectDefinition.
	 * 
	 * This method considers default behavior for - reports steps and returns
	 * visible objects only.
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * @param handlePopup
	 *            A boolean type variable that represents whether method needs
	 *            to handle popup or not.
	 * 
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(TestCases testCase, String locatorType, String locatorValue,
			boolean handlePopup) {

		StringBuilder objName = new StringBuilder(locatorType);
		objName = objName.append(":");
		objName = objName.append(locatorValue);
		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();
		objects.put(objName.toString(), mObject);
		return MobileUtils.getMobElements(objects, testCase, objName.toString(), handlePopup, true, false);
	}

	/**
	 * Returns a List of Mobile elements, Based on the Object property in
	 * objectDefinition HashMap. The testCase argument is TestCases type variable
	 * that holds the values that has to pass amount method like Webdriver
	 * instance. The objectDefinition argument is a HashMap that contains the object
	 * definition read from the JSON files. The objName argument is the Key in
	 * the HashMap objectDefinition.
	 * 
	 * This method considers default behavior for - reports steps and returns
	 * visible objects only.
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * @param handlePopup
	 *            A boolean type variable that represents whether method needs
	 *            to handle popup or not.
	 * 
	 * @param enableStepReporting
	 *            A boolean type variable that represents whether method needs
	 *            to report step failure or not.
	 * 
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(TestCases testCase, String locatorType, String locatorValue,
			boolean handlePopup, boolean enableStepReporting) {

		StringBuilder objName = new StringBuilder(locatorType);
		objName = objName.append(":");
		objName = objName.append(locatorValue);
		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();
		objects.put(objName.toString(), mObject);
		return MobileUtils.getMobElements(objects, testCase, objName.toString(), handlePopup, enableStepReporting,
				false);
	}

	/**
	 * Returns a List of Mobile elements, Based on the Object property in
	 * objectDefinition HashMap. The testCase argument is TestCases type variable
	 * that holds the values that has to pass amount method like Webdriver
	 * instance. The objectDefinition argument is a HashMap that contains the object
	 * definition read from the JSON files. The objName argument is the Key in
	 * the HashMap objectDefinition.
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * @param handlePopup
	 *            A boolean type variable that represents whether method needs
	 *            to handle popup or not.
	 * 
	 * @param enableStepReporting
	 *            A boolean type variable that represents whether method needs
	 *            to report step failure or not.
	 * 
	 * @param includeInvisibleElements
	 *            A boolean type variable that represents whether method needs
	 *            to include invisible elements or not.
	 * 
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(TestCases testCase, String locatorType, String locatorValue,
			boolean handlePopup, boolean enableStepReporting, boolean includeInvisibleElements) {

		StringBuilder objName = new StringBuilder(locatorType);
		objName = objName.append(":");
		objName = objName.append(locatorValue);
		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();
		objects.put(objName.toString(), mObject);
		return MobileUtils.getMobElements(objects, testCase, objName.toString(), handlePopup, enableStepReporting,
				includeInvisibleElements);
	}

	/**
	 * Returns a List of mobile elements, Based on the Object property in
	 * objectDefinition HashMap.
	 * 
	 * This method considers default behavior for - handles pop - true, reports
	 * steps - true and returns visible objects only
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName) {
		return getMobElements(objectDefinition, testCase, objName, true, true, false);
	}

	/**
	 * Returns a List of mobile elements, Based on the Object property in
	 * objectDefinition HashMap.
	 * 
	 * This method considers default behavior for - reports steps - true and
	 * returns visible objects only
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @param handlePopup
	 *            A boolean type variable that represents whether the method
	 *            should handle popups or not
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, boolean handlePopup) {
		return getMobElements(objectDefinition, testCase, objName, handlePopup, true, false);
	}

	/**
	 * Returns a List of mobile elements, Based on the Object property in
	 * objectDefinition HashMap.
	 * 
	 * This method considers default behavior for - reports steps - true and
	 * returns visible objects only
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @param handlePopup
	 *            A boolean type variable that represents whether the method
	 *            should handle popups or not
	 * 
	 * @param enableStepReporting
	 *            A boolean type variable that represents whether step failure
	 *            should be reported or not
	 * 
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	public static List<WebElement> getMobElements(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, boolean handlePopup, boolean enableStepReporting) {
		return getMobElements(objectDefinition, testCase, objName, handlePopup, enableStepReporting, false);
	}

	/**
	 * Returns a List of mobile elements, Based on the Object property in
	 * objectDefinition HashMap.
	 * 
	 * <p>
	 * This method returns a List of Mobile elements.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @param handlePopup
	 *            A boolean type variable that represents whether the method
	 *            should handle popups or not
	 * 
	 * @param enableStepReporting
	 *            A boolean type variable that represents whether step failure
	 *            should be reported or not
	 * 
	 * @param includeInvisibleElements
	 *            A boolean type variable that represents whether we need to
	 *            include invisible object as well.
	 * @author E880579
	 * @return List&lt;WebElement&gt;
	 */

	@SuppressWarnings("unchecked")
	public static List<WebElement> getMobElements(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, boolean handlePopup, boolean enableStepReporting, boolean includeInvisibleElements) {

		StringBuilder methodName = new StringBuilder("Get Mobile Elements : '");

		methodName = methodName.append(objName);

		CustomDriver driver = testCase.getMobileDriver();
		List<WebElement> elements = new ArrayList<>();
		FluentWait<WebDriver> fWait = null;

		if (driver == null) {
			methodName = methodName.append("', Web Driver should be instantiated before getting element.");
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FUNCTIONAL_FAILURE, methodName.toString());
		} else {

			fWait = new FluentWait<WebDriver>(driver);

//			fWait.withTimeout(FrameworkGlobalVariables.LONG_WAIT, TimeUnit.MILLISECONDS);
//			fWait.pollingEvery(FrameworkGlobalVariables.POLLING_WAIT, TimeUnit.MILLISECONDS);
			
			fWait.withTimeout(Duration.ofMillis(FrameworkGlobalVariables.LONG_WAIT));
			fWait.pollingEvery(Duration.ofMillis(FrameworkGlobalVariables.POLLING_WAIT));
			
			
			
			fWait.ignoring(NoSuchElementException.class);
			fWait.ignoring(WebDriverException.class);
			fWait.ignoring(SocketException.class);
			fWait.ignoring(IOException.class);

			if (objectDefinition != null) {
				if (objectDefinition.containsKey(objName)) {
					MobileObject objDesc = objectDefinition.get(objName);
					try {
						if (objDesc.getLocationType().equalsIgnoreCase(MobileObject.NAME)) {
							if (testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME)
									.equalsIgnoreCase(Mobile.IOS)) {
								elements = (List<WebElement>) (Object) ((IOSDriver<MobileElement>) driver)
										.findElementsByAccessibilityId(objDesc.getLocationValue());
							} else {
								elements = (List<WebElement>) (Object) ((AndroidDriver<MobileElement>) driver)
										.findElementsByAccessibilityId(objDesc.getLocationValue());
							}
						} else {
							elements = fWait.until(ExpectedConditions
									.presenceOfAllElementsLocatedBy(objDesc.getLocator(testCase.doUseXCUITest())));
							// .visibilityOfAllElementsLocatedBy(objDesc.getLocator(testCase.doUseXCUITest())));
						}

						if (testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME)
								.equalsIgnoreCase(Mobile.IOS)) {
							if (includeInvisibleElements) {

							} else {
								Iterator<WebElement> iter = elements.iterator();

								while (iter.hasNext()) {
									if (iter.next().isDisplayed()) {
										// Do nothing
									} else {
										iter.remove();
									}
								}
							}
						}

						if (elements.size() == 0) {
							if (enableStepReporting) {
								methodName = methodName.append(" No elements found that are visible.'");
								Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, methodName.toString());
							}
						}

					} catch (TimeoutException e) {

						if (handlePopup) {
							if (Mobile.handlePopups(testCase.getMobileDriver(),
									testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME))) {
								return getMobElements(objectDefinition, testCase, objName, handlePopup, enableStepReporting,
										includeInvisibleElements);
							} else {
								if (enableStepReporting) {
									Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
											methodName.toString());
								}
							}
						} else {
							if (enableStepReporting) {
								methodName = methodName.append("', Element identification got timed out.");
								Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, methodName.toString());
							}
						}

					} catch (Exception e) {
						if (enableStepReporting) {
							methodName = methodName.append("', Unexpected Error occured - ");
							methodName = methodName.append(FrameworkUtils.getMessage(e));
							Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, methodName.toString());
						}

					}

				} else {
					if (enableStepReporting) {
						methodName = methodName.append("' object definition not present.");
						Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, methodName.toString());
					}
				}

			} else {
				if (enableStepReporting) {
					methodName = methodName.append(objName);
					methodName = methodName.append("', Element properties should be loaded before getting element.");
					Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FUNCTIONAL_FAILURE,
							methodName.toString());
				}
			}
		}

		methodName = null;

		return elements;
	}

	/**
	 * GetMobElement is use to identify object on screen.
	 * <p>
	 * This method returns a WebElement based on the object property provided,
	 * if the object didn't appeared it returns null.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * @param handlePopups
	 *            boolean variable first represents popup handler
	 * @param reportStep
	 *            boolean variable represents report step.
	 * 
	 * @author E880579
	 * @return MobileElement/null
	 */

	public static @CheckForNull MobileElement getMobElement(TestCases testCase, String locatorType, String locatorValue,
			boolean handlePopups, boolean reportStep) {

		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		objects.put(objectName.toString(), mObject);
		return getMobElement(objects, testCase, objectName.toString(), handlePopups, reportStep);
	}

	/**
	 * GetMobElement is use to identify object on screen.
	 * <p>
	 * This method returns a WebElement based on the object property provided,
	 * if the object didn't appeared it returns null.
	 * 
	 * This method represents default behavior of popup - true and report step -
	 * true
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * 
	 * @author E880579
	 * @return MobileElement/null
	 */

	public static @CheckForNull MobileElement getMobElement(TestCases testCase, String locatorType,
			String locatorValue) {

		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		objects.put(objectName.toString(), mObject);
		return getMobElement(objects, testCase, objectName.toString(), true, true);
	}

	/**
	 * GetMobElement is use to identify object on screen.
	 * <p>
	 * This method returns a WebElement based on the object property provided,
	 * if the object didn't appeared it returns null.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            A String type variable that represents the Locator type
	 * @param locatorValue
	 *            A String type variable that represents the Locator value
	 * 
	 * @param handlePopups
	 *            A boolean type variable that represents the handle popups
	 * @author E880579
	 * @return MobileElement/null
	 */

	public static @CheckForNull MobileElement getMobElement(TestCases testCase, String locatorType, String locatorValue,
			boolean handlePopups) {

		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> objects = new HashMap<String, MobileObject>();

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		objects.put(objectName.toString(), mObject);
		return getMobElement(objects, testCase, objectName.toString(), handlePopups, true);
	}

	/**
	 * GetMobElement is use to identify object on screen.
	 * <p>
	 * This method returns a WebElement based on the object property provided,
	 * if the object didn't appeared it returns null.
	 * 
	 * Method considers handlePopups - true and reportStep - true
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @author E880579
	 * @return MobileElement/null
	 */

	public static @CheckForNull MobileElement getMobElement(HashMap<String, MobileObject> objectDefinition,
			TestCases testCase, String objName) {
		return getMobElement(objectDefinition, testCase, objName, true, true);
	}

	/**
	 * GetMobElement is use to identify object on screen.
	 * <p>
	 * This method returns a WebElement based on the object property provided,
	 * if the object didn't appeared it returns null.
	 * 
	 * Method considers handlePopups - true and reportStep - true
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @param handlePopups
	 *            boolean variable first represents popup handler
	 * 
	 * @author E880579
	 * @return MobileElement/null
	 */

	public static @CheckForNull MobileElement getMobElement(HashMap<String, MobileObject> objectDefinition,
			TestCases testCase, String objName, boolean handlePopups) {
		return getMobElement(objectDefinition, testCase, objName, handlePopups, true);
	}

	/**
	 * GetMobElement is use to identify object on screen.
	 * <p>
	 * This method returns a WebElement based on the object property provided,
	 * if the object didn't appeared it returns null.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * @param handlePopups
	 *            boolean variable first represents popup handler
	 * @param reportStep
	 *            boolean variable represents report step.
	 * 
	 * @author E880579
	 * @return MobileElement/null
	 */

	public static @CheckForNull MobileElement getMobElement(HashMap<String, MobileObject> objectDefinition,
			TestCases testCase, String objName, boolean handlePopups, boolean reportStep) {

		MobileElement element = null;
		By locator = null;

		CustomDriver driver = testCase.getMobileDriver();

		long startTime = System.currentTimeMillis();

		FluentWait<WebDriver> fWait = null;

		if (driver == null) {
			return element;
		} else {

			StringBuilder stepMessage = new StringBuilder("Get Mobile Element : ");
			stepMessage = stepMessage.append(objName);

			fWait = new FluentWait<WebDriver>(driver);

			//fWait.withTimeout(FrameworkGlobalVariables.LONG_WAIT, TimeUnit.MILLISECONDS);
			//fWait.pollingEvery(FrameworkGlobalVariables.POLLING_WAIT, TimeUnit.MILLISECONDS);
			
			fWait.withTimeout(Duration.ofMillis(FrameworkGlobalVariables.LONG_WAIT));
			fWait.pollingEvery(Duration.ofMillis(FrameworkGlobalVariables.POLLING_WAIT));
			
			
			
			fWait.ignoring(NoSuchElementException.class);
			fWait.ignoring(SocketException.class);
			fWait.ignoring(WebDriverException.class);
			fWait.ignoring(IOException.class);

			if (objectDefinition != null) {
				if (objectDefinition.containsKey(objName)) {
					MobileObject objDesc = objectDefinition.get(objName);

					try {
						if (objDesc.getLocationType().equalsIgnoreCase(MobileObject.NAME)) {
							if (testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME)
									.equalsIgnoreCase(Mobile.IOS)) {
								element = fWait.until(new Function<WebDriver, MobileElement>() {
									@SuppressWarnings("unchecked")
									@Override
									public MobileElement apply(WebDriver driver) {
										List<MobileElement> elements = ((IOSDriver<MobileElement>) driver)
												.findElementsByAccessibilityId(objDesc.getLocationValue());

										MobileElement element = null;

										for (int index = 0; index < elements.size(); ++index) {
											if (elements.get(index).isDisplayed()) {
												element = elements.get(index);
												break;
											}
										}

										return element;
									}
								});
							} else {
								element = fWait.until(new Function<WebDriver, MobileElement>() {
									@SuppressWarnings("unchecked")
									@Override
									public MobileElement apply(WebDriver driver) {
										return ((AndroidDriver<MobileElement>) driver)
												.findElementByAccessibilityId(objDesc.getLocationValue());
									}
								});
							}

						} else {

							if (isRunningOnAndroid(testCase)) {
								locator = objDesc.getLocator();
							} else {
								locator = objDesc.getLocator(testCase.doUseXCUITest());
							}

							if (isRunningOnAndroid(testCase)) {
								element = (MobileElement) fWait
										.until(ExpectedConditions.visibilityOfElementLocated(locator));
							} else {
								List<WebElement> elements = fWait
										.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
								element = null;
								for (int index = 0; index < elements.size(); ++index) {
									if (elements.get(index).isDisplayed()) {
										element = (MobileElement) elements.get(index);
										break;
									}
								}
							}
						}
					} catch (TimeoutException e) {

						stepMessage = stepMessage.append("', Element identification got timed out.");

						handlePopups = false;
						if (handlePopups) {
							if (Mobile.handlePopups(testCase.getMobileDriver(),
									testCase.getTestCaseInputs().getInputValue(TestCaseInputs.OS_NAME))) {
								return getMobElement(objectDefinition, testCase, objName, handlePopups, reportStep);
							} else {
								if (reportStep) {
									Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
											stepMessage.toString());
								}
							}
						} else {
							if (reportStep) {
								Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
							}
							return null;
						}
					} catch (UnreachableBrowserException e) {
						Assert.fail();
					} catch (Exception e) {
						if (reportStep) {
							stepMessage = stepMessage.append("', Unexpected Error occured - ");
							stepMessage = stepMessage.append(FrameworkUtils.getMessage(e));
							Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
						}

					} finally {

						if (FrameworkGlobalVariables.ENABLE_PERFORMANCE_DATA) {

							if (locator == null) {
								System.out.println(" locator : Name. Time taken "
										+ (System.currentTimeMillis() - startTime) + " msecs.");

								Keyword.ReportStep_Pass(testCase,
										"Time Take to identify element using Locator : Name. Time taken : "
												+ (System.currentTimeMillis() - startTime) + " msecs.");
							} else {
								System.out.println(" locator : " + locator.toString() + " Time taken "
										+ (System.currentTimeMillis() - startTime) + " msecs.");

								Keyword.ReportStep_Pass(testCase,
										"Time Take to identify element using Locator : " + locator.toString()
										+ ". Time taken : " + (System.currentTimeMillis() - startTime)
										+ " msecs.");
							}
						}

					}

				} else {
					stepMessage = stepMessage.append("' object definition not present.");
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
				}

			} else {
				stepMessage = stepMessage.append("', Element properties should be loaded before getting element.");
				Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FUNCTIONAL_FAILURE,
						stepMessage.toString());
			}

			stepMessage = null;
		}

		return element;
	}

	/**
	 * Click on element method clicks on an element.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param locatorType
	 *            String representing Locator type.
	 * 
	 * @param locatorValue
	 *            String representing Locator Value.
	 * 
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, String locatorType, String locatorValue) {
		return clickOnElement(testCase, locatorType, locatorValue, true, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param locatorType
	 *            String representing Locator type.
	 * 
	 * @param locatorValue
	 *            String representing Locator Value.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, String locatorType, String locatorValue,
			boolean handlerPopUp) {
		return clickOnElement(testCase, locatorType, locatorValue, handlerPopUp, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param locatorType
	 *            String representing Locator type.
	 * 
	 * @param locatorValue
	 *            String representing Locator Value.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * @param reportStep
	 *            boolean variable represents report step.
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, String locatorType, String locatorValue,
			boolean handlerPopUp, boolean reportStep) {

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> object = new HashMap<String, MobileObject>();
		object.put(objectName.toString(), mObject);

		return MobileUtils.clickOnElement(object, testCase, objectName.toString(), handlerPopUp, reportStep);
	}

	/**
	 * Click on element method clicks on an element.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param locatorType
	 *            String representing Locator type.
	 * 
	 * @param locatorValue
	 *            String representing Locator Value.
	 * 
	 * @param message
	 *            Optional String to provide custom message.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, String locatorType, String locatorValue, String message) {
		return clickOnElement(testCase, locatorType, locatorValue, message, true, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param locatorType
	 *            String representing Locator type.
	 * 
	 * @param locatorValue
	 *            String representing Locator Value.
	 * 
	 * @param message
	 *            Optional String to provide custom message.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, String locatorType, String locatorValue, String message,
			boolean handlerPopUp) {
		return clickOnElement(testCase, locatorType, locatorValue, message, handlerPopUp, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * Click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param locatorType
	 *            String representing Locator type.
	 * 
	 * @param locatorValue
	 *            String representing Locator Value.
	 * 
	 * @param message
	 *            Optional String to provide custom message.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * @param reportStep
	 *            boolean variable represents report step.
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, String locatorType, String locatorValue, String message,
			boolean handlerPopUp, boolean reportStep) {

		StringBuilder objectName = new StringBuilder(locatorType);
		objectName = objectName.append(":");
		objectName = objectName.append(locatorValue);

		MobileObject mObject = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> object = new HashMap<String, MobileObject>();
		object.put(objectName.toString(), mObject);

		return MobileUtils.clickOnElement(object, testCase, objectName.toString(), message, handlerPopUp, reportStep);
	}

	/**
	 * Click on element method clicks on an element.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * 
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * 
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName) {
		
		return clickOnElement(objectDefinition, testCase, objName, true, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * 
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * 
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * @param reportStep
	 *            boolean variable represents report step.
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName,
			boolean handlerPopUp, boolean reportStep) {

		boolean success = true;
		WebElement element;

		StringBuilder stepMessage = new StringBuilder("Click on Element : ");

		try {

			element = getMobElement(objectDefinition, testCase, objName, handlerPopUp, reportStep);

			if (element != null) {
				element.click();
				if (reportStep) {
					stepMessage = stepMessage.append("Successfully click on ");
					stepMessage = stepMessage.append(objName);
					Keyword.ReportStep_Pass(testCase, stepMessage.toString());
				}

			} else {
				if (reportStep) {
					stepMessage = stepMessage.append("Element not found ");
					stepMessage = stepMessage.append(objName);
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
				}

				success = false;
			}

		} catch (Exception e) {
			if (reportStep) {
				stepMessage = stepMessage.append("Error occured while Click on Element ");
				stepMessage = stepMessage.append(objName);
				stepMessage = stepMessage.append(" Error Message - ");
				stepMessage = stepMessage.append(FrameworkUtils.getMessage(e));
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
			}else{
				Keyword.ReportStep_Fail(testCase, FailType.NO_FAILURE, stepMessage.toString());
			}

			success = false;
		} finally {
			stepMessage = null;
		}

		return success;
	}

	/**
	 * Click on element method clicks on an element.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param objectDefinition
	 *            HashMap that contains the object definition.
	 * 
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection.
	 * 
	 * @param message
	 *            String to provide custom message.
	 * 
	 * 
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName,
			String message) {
		return clickOnElement(objectDefinition, testCase, objName, message, true, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param objectDefinition
	 *            HashMap that contains the object definition.
	 * 
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection.
	 * 
	 * @param message
	 *            String to provide custom message.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName,
			String message, boolean handlerPopUp) {
		return clickOnElement(objectDefinition, testCase, objName, message, handlerPopUp, true);
	}

	/**
	 * Click on element method clicks on an element.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param objectDefinition
	 *            HashMap that contains the object definition.
	 * 
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection.
	 * 
	 * @param message
	 *            String to provide custom message.
	 * 
	 * @param handlerPopUp
	 *            boolean variable represents popup handler
	 * 
	 * @param reportStep
	 *            boolean variable represents report step.
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String objName,
			String message, boolean handlerPopUp, boolean reportStep) {

		boolean success = true;

		StringBuilder stepMessage = new StringBuilder(message);
		stepMessage = stepMessage.append(" Click on Element : ");

		WebElement element = getMobElement(objectDefinition, testCase, objName, handlerPopUp, false);

		if (element != null) {
			try {
				element.click();

				stepMessage = stepMessage.append("Click successful on ");
				stepMessage = stepMessage.append(objName);

				Keyword.ReportStep_Pass(testCase, stepMessage.toString());

			} catch (Exception e) {
				stepMessage = stepMessage.append("Click unsuccessful on ");
				stepMessage = stepMessage.append(objName);
				stepMessage = stepMessage.append(" Error Message ");
				stepMessage = stepMessage.append(FrameworkUtils.getMessage(e));
				if (reportStep) {
					Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
				}else{
					Keyword.ReportStep_Fail(testCase, FailType.NO_FAILURE, stepMessage.toString());
				}

				success = false;
			}
		} else {
			stepMessage = stepMessage.append("Click unsuccessful on ");
			stepMessage = stepMessage.append(objName);
			stepMessage = stepMessage.append(" Element not found.");
			
			if (reportStep) {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
			}else{
				Keyword.ReportStep_Fail(testCase, FailType.NO_FAILURE, stepMessage.toString());
			}

			success = false;
		}

		element = null;

		return success;
	}

	/**
	 * Click on element method clicks on an element.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of
	 * click operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method.
	 * 
	 * @param mobileElement
	 *            MobileElement type variable that represent the object to click
	 *            on.
	 * 
	 * @param message
	 *            String to provide custom message.
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean clickOnElement(TestCases testCase, MobileElement mobileElement, String message) {

		boolean success = true;

		StringBuilder stepMessage = new StringBuilder(message);
		stepMessage = stepMessage.append(" Click on Element : ");

		if (mobileElement != null) {
			try {

				mobileElement.click();

				stepMessage = stepMessage.append("Click successful on ");
				stepMessage = stepMessage.append(mobileElement);

				Keyword.ReportStep_Pass(testCase, stepMessage.toString());

			} catch (Exception e) {

				stepMessage = stepMessage.append("Click unsuccessful on ");
				stepMessage = stepMessage.append(mobileElement);
				stepMessage = stepMessage.append(" Error Message ");
				stepMessage = stepMessage.append(FrameworkUtils.getMessage(e));

				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
				success = false;
			}
		} else {

			stepMessage = stepMessage.append("Click unsuccessful on ");
			stepMessage = stepMessage.append(mobileElement);
			stepMessage = stepMessage.append(" Element not found.");
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
			success = false;
		}

		stepMessage = null;

		return success;
	}

	// TODO: Deprecate in next release

	@Deprecated
	public static boolean setPickerValueIOS(TestCases testCase, String locatorType, String locatorValue, String value) {
		MobileObject object = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> mObject = new HashMap<String, MobileObject>();
		mObject.put(locatorType + ":" + locatorValue, object);
		return MobileUtils.setPickerValueIOS(mObject, testCase, locatorType + ":" + locatorValue, value);
	}

	// TODO: Deprecate in next release

	@Deprecated
	public static boolean setPickerValueIOS(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, String value) {
		boolean success = true;

		if (isRunningOnAndroid(testCase)) {
			success = false;
		} else {
			StringBuilder stepMessage = new StringBuilder();
			stepMessage = stepMessage.append(" Set Value to Element : ");
			stepMessage = stepMessage.append(objName);

			WebElement element = getMobElement(objectDefinition, testCase, objName);

			if (element != null) {
				try {
					((IOSElement) element).setValue(value);
				} catch (Exception e) {
					stepMessage = stepMessage.append(" Unable to Set Value on element");
					stepMessage = stepMessage.append(FrameworkUtils.getMessage(e));
					success = false;
				}
			} else {
				stepMessage = stepMessage.append(" Unable to Set Value on element - Element not found.");
				success = false;
			}

			element = null;

			if (!success) {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());

			}
		}

		return success;
	}

	/**
	 * Set Value to Element is use to set value to a text box or a wheel picker
	 * on IOS.
	 * <p>
	 * This method returns a boolean value specifying the success/failure of Set
	 * operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param locatorType
	 *            String representing Locator type.
	 * @param locatorValue
	 *            String representing Locator Value.
	 * @param value
	 *            A String type value to be set on object.
	 * 
	 *            * @param message Optional String to provide custom message
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean setValueToElement(TestCases testCase, String locatorType, String locatorValue, String value,
			String... message) {

		MobileObject object = new MobileObject(locatorType, locatorValue);
		HashMap<String, MobileObject> mObject = new HashMap<String, MobileObject>();
		mObject.put(locatorType + ":" + locatorValue, object);
		return MobileUtils.setValueToElement(mObject, testCase, locatorType + ":" + locatorValue, value, message);
	}

	/**
	 * Set Value to Element is use to set value to a text box or a wheel picker
	 * on IOS.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of Set
	 * operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * 
	 * @param objectDefinition
	 *            HashMap that contains the object definition
	 * 
	 * @param objName
	 *            A String type variable that represents the Object definition
	 *            in objectDefinition collection
	 * 
	 * @param value
	 *            A String type value to be set on object.
	 * 
	 * @param message
	 *            Optional String to provide custom message
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean setValueToElement(HashMap<String, MobileObject> objectDefinition, TestCases testCase,
			String objName, String value, String... message) {

		StringBuilder stepMessage = new StringBuilder(message.length > 0 ? message[0] : FrameworkGlobalVariables.BLANK);
		stepMessage = stepMessage.append(" Set Value to Element : ");
		stepMessage = stepMessage.append(objName);

		boolean success = true;

		WebElement element = getMobElement(objectDefinition, testCase, objName);

		if (element != null) {

			for (int counter = 0; counter < 3; ++counter) {
				try {
					element.sendKeys(value);
					break;
				} catch (Exception e) {
					if (counter <= 2) {
						element.clear();
					} else {
						stepMessage = stepMessage.append(" Unable to Set Value on element. Error message - ");
						stepMessage = stepMessage.append(FrameworkUtils.getMessage(e));
						success = false;
					}
				}
			}

		} else {
			stepMessage = stepMessage.append(" Unable to Set Value on element - Element not found.");
			success = false;
		}

		element = null;

		if (!success) {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, stepMessage.toString());
		}

		return success;
	}

	/**
	 * Set Value to Element is use to set value to a text box or a wheel picker
	 * on IOS.
	 * 
	 * <p>
	 * This method returns a boolean value specifying the success/failure of Set
	 * operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * 
	 * @param mobElement
	 *            MobileElement that contains the object reference of element to
	 *            set value on.
	 * 
	 * @param value
	 *            A String type value to be set on object.
	 * 
	 * @param message
	 *            Optional String to provide custom message
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean setValueToElement(TestCases testCase, MobileElement mobElement, String value,
			String... message) {
		boolean success = true;

		if (mobElement != null) {

			for (int counter = 0; counter < 3; ++counter) {
				try {
					mobElement.sendKeys(value);
					break;
				} catch (Exception e) {
					if (counter <= 2) {
						mobElement.clear();
					} else {
						Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
								String.format(
										"Set Value to Element : [%s] unable to Set Value on element. Reason - %s.",
										mobElement, e.getLocalizedMessage().split("Command duration")[0]));
						success = false;
					}
				}
			}

		} else {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
					"Set Value to Element : Unable to Set Value on element - Element not found.");
			success = false;
		}

		return success;
	}

	/**
	 * MinimizeApp method is use to sent the app in background for predefined
	 * amount of time.
	 * 
	 * Returns a boolean value, representing success/failure of App. minimize
	 * operation.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * 
	 * @param durationInSeconds
	 *            Int type variable that represent the time in seconds to run
	 *            the app in background.
	 * 
	 * @author E880579
	 * @return True/False
	 */

	public static boolean minimizeApp(TestCases testCase, int durationInSeconds) {
		boolean success = true;
		CustomDriver driver = testCase.getMobileDriver();

		if (driver == null) {
			success = false;

		} else {
			try {

				Keyword.ReportStep_Pass(testCase,
						"[Minimize App] : App is sent to back ground for - " + durationInSeconds + " Second(s).");

				TestCaseInputs inputs = testCase.getTestCaseInputs();

				if (inputs.isRunningOn("Perfecto")) {

					if (inputs.getInputValue(TestCaseInputs.OS_NAME).equalsIgnoreCase(Mobile.IOS)) {
						Instant start = Instant.now();
						Instant end = Instant.now().plusSeconds(durationInSeconds);
						Duration duration = Duration.between(start, end);
						driver.runAppInBackground(duration);
					} else {
						driver.closeApp();
						Thread.sleep(durationInSeconds * 1000);
						driver.launchApp();
					}
				} else {
					Instant start = Instant.now();
					Instant end = Instant.now().plusSeconds(durationInSeconds);
					Duration duration = Duration.between(start, end);
					((CustomDriver) driver).runAppInBackground(duration);
				}

			} catch (Exception e) {
				FrameworkGlobalVariables.logger4J.logWarn("Minimize App : " + FrameworkUtils.getMessage(e));
			}
		}

		return success;
	}

	/**
	 * Returns an AppiumDriver reference after launching the app. The testCase
	 * argument is TestCases type variable that holds the values that has to
	 * pass among methods like Webdriver instance. The input argument is a
	 * String array that contains all the desired capabilities for the app.
	 * <p>
	 * This method returns an Appium reference for the app launched.
	 * 
	 * @param inputs
	 *            Collection of test data.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * 
	 * @param doAutoAcceptAlert
	 *            boolean parameter to represent the auto accept capability of
	 *            appium.
	 * 
	 * @param doFullReset
	 *            Optional boolean parameter to represent the full Reset
	 *            capability of appium. Default is true.
	 * 
	 * @author E880579
	 * @return AppiumDriver/Null
	 */

	public static @CheckForNull boolean launchApplication(TestCaseInputs inputs, TestCases testCase,
			boolean doAutoAcceptAlert, boolean... doFullReset) {

		boolean flag = true;

		if (inputs.isAccountAquisitionAutomatic() && !testCase.isAcquisitionSuccessful) {
			return false;
		}

		MobileDriverFactory driverFactory = new MobileDriverFactory();

		try {
			if (doFullReset.length > 0) {
				flag = driverFactory.instantiateDriver(inputs, testCase, doAutoAcceptAlert, doFullReset[0]);
			} else {
				flag = driverFactory.instantiateDriver(inputs, testCase, doAutoAcceptAlert, false);
			}
		} catch (Exception e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Error Occured while launching application. Error message - "
							+ e.getLocalizedMessage().split("Command duration")[0]);
			System.out.println(e.getLocalizedMessage());
		}

		return flag;
	}

	/**
	 * Returns HashMap containing object definition.
	 * 
	 * <p>
	 * This method returns a collection of Object definition in HashMap format.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param screenName
	 *            A String type variable that represents the application screen
	 *            name to load.
	 * 
	 * @author E880579
	 * @return HashMap &lt;String, MobileObject&gt;
	 */

	public static HashMap<String, MobileObject> loadObjectFile(TestCases testCase, String screenName) {

		String line;
		String fieldName;
		JSONObject jsonObj = null;
		JSONObject fieldValue = null;
		BufferedReader br = null;

		String platform = testCase.getPlatform();

		Iterator<String> fields;

		HashMap<String, MobileObject> fieldDefinition = new HashMap<String, MobileObject>();

		MobileObject mObj;

		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(
							FrameworkUtils.getFrameworkConstant("OBJ_DEFINITION_FLD") + "/" + screenName + ".json"),
					"UTF8"));

			while ((line = br.readLine()) != null) {
				line = line.trim();
				try {
					jsonObj = new JSONObject(line);
				} catch (JSONException e) {
					jsonObj = new JSONObject(line.substring(1, line.length()));
				}
				fields = jsonObj.keys();
				while (fields.hasNext()) {
					fieldName = fields.next();
					fieldValue = (JSONObject) ((JSONObject) jsonObj.get(fieldName)).get(platform);
					mObj = new MobileObject(fieldValue.getString("Locator_Type"),
							fieldValue.getString("Locator_Value"));
					fieldDefinition.put(fieldName, mObj);
				}
			}
		} catch (Exception e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Load Object File : Error occured - " + e.getLocalizedMessage().split("Command duration")[0]);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
							"Load Object File : Error occured - Not able to close the file - "
									+ e.getLocalizedMessage().split("Command duration")[0]);
				}
			}
		}

		return fieldDefinition;
	}

	/**
	 * Returns HashMap containing object definition. The testCase argument is
	 * TestCases type variable that holds the values that has to pass among
	 * methods like Webdriver instance. The screenName argument is a String
	 * representing Screen to load
	 * <p>
	 * This method returns an Appium reference for the app launched.
	 * 
	 * @param testCase
	 *            TestCases type variable that holds the values that has to pass
	 *            amount method
	 * @param screenName
	 *            A String type variable that represents the application screen
	 *            name to load.
	 * @param platform
	 *            A String type variable that represents the platform type.
	 * @author E880579
	 * @return HashMap&lt;String, MobileObject&gt;
	 */

	public static HashMap<String, MobileObject> loadObjectFile(TestCases testCase, String screenName, String platform) {

		String line;
		String fieldName;
		JSONObject jsonObj = null;
		JSONObject fieldValue = null;
		BufferedReader br = null;

		Iterator<String> fields;

		HashMap<String, MobileObject> fieldDefinition = new HashMap<String, MobileObject>();

		MobileObject mObj;

		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(
							FrameworkUtils.getFrameworkConstant("OBJ_DEFINITION_FLD") + "/" + screenName + ".json"),
					"UTF8"));

			while ((line = br.readLine()) != null) {
				line = line.trim();
				try {
					jsonObj = new JSONObject(line);
				} catch (JSONException e) {
					jsonObj = new JSONObject(line.substring(1, line.length()));
				}
				fields = jsonObj.keys();
				while (fields.hasNext()) {
					fieldName = fields.next();
					fieldValue = (JSONObject) ((JSONObject) jsonObj.get(fieldName)).get(platform);
					mObj = new MobileObject(fieldValue.getString("Locator_Type"),
							fieldValue.getString("Locator_Value"));
					fieldDefinition.put(fieldName, mObj);
				}
			}
		} catch (FileNotFoundException e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Load Object File : Error occured - " + e.getLocalizedMessage().split("Command duration")[0]);
		} catch (IOException e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Load Object File : Error occured - " + e.getLocalizedMessage().split("Command duration")[0]);
		} catch (JSONException e) {
			Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
					"Load Object File : Error occured - " + e.getLocalizedMessage().split("Command duration")[0]);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Keyword.ReportStep_Fail_WithOut_ScreenShot(testCase, FailType.FRAMEWORK_CONFIGURATION,
							"Load Object File : Error occured - Not able to close the file - "
									+ e.getLocalizedMessage().split("Command duration")[0]);
				}
			}
		}

		return fieldDefinition;
	}

	@Deprecated
	public static boolean fwait1(HashMap<String, MobileObject> objectDefinition, TestCases testCase, String element,
			int timeout) {
		MobileObject mObject = objectDefinition.get(element);
		FluentWait<CustomDriver> fWait = new FluentWait<CustomDriver>(testCase.getMobileDriver());
		
		//fWait.withTimeout(timeout, TimeUnit.SECONDS);
		//fWait.pollingEvery(500, TimeUnit.MILLISECONDS);
		
		fWait.withTimeout(Duration.ofSeconds(timeout));
		fWait.pollingEvery(Duration.ofMillis(500));
		
		fWait.ignoring(WebDriverException.class);
		fWait.ignoring(SocketException.class);
		fWait.ignoring(NoSuchElementException.class);
		try {
			fWait.until(
					ExpectedConditions.visibilityOfAllElementsLocatedBy(mObject.getLocator(testCase.doUseXCUITest())));
		} catch (TimeoutException e) {
			return false;
		} catch (Exception e) {
			Keyword.ReportStep_Fail(testCase, FailType.FRAMEWORK_CONFIGURATION,
					" fwait - " + e.getLocalizedMessage().split("Command duration")[0]);
			return false;
		}
		return true;

	}

	@Deprecated
	public static boolean adbProcessor(TestCases testCase, String command) {
		String line = "null";
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		try {
			pr = run.exec(command);
			try {
				pr.waitFor();
			} catch (InterruptedException e1) {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
						"Interrupted Exception caused by " + e1.getMessage());
			}
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			try {
				while ((line = buf.readLine()) != null) {
					if (line.contains("Success")) {
						Keyword.ReportStep_Pass(testCase, line);
					} else if (line.contains("Failure")) {
						Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE, line);
						return false;
					}
				}
			} catch (IOException e) {
				Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
						"IO Exception caused by " + e.getLocalizedMessage().split("Command duration")[0]);
			}
		} catch (IOException e) {
			Keyword.ReportStep_Fail(testCase, FailType.FUNCTIONAL_FAILURE,
					"IO Exception caused by " + e.getLocalizedMessage().split("Command duration")[0]);
		}
		return true;
	}

}
