package com.demo.suiteExecutor;

import com.demo.commons.coreframework.SuiteUtils;

public class SuiteExecutor {

	/**
	 * @param commandLineArguments
	 * @throws Exception
	 */
	public static void main(String[] commandLineArguments) throws Exception {

	commandLineArguments = new String[] {
						"--useXCUITest", "true",
							//	"--publishResult",
						"--deviceCloudProviderCredentials",
						
							+ "Perfecto::resiqa0@gmail.com:Password1,"

					// "--setResultFolder", "D:/ExecutionResultforJasper",
					"--appToInstall", "IOS:OPTIK_IOS_DEMO_3_2,Android:optiktvmarch2",
					"--groups",
					"Optiktvguestflow"	
	};

		try 
		{

			SuiteUtils suiteUtils = SuiteUtils.getTestSuite(commandLineArguments);
			suiteUtils.executeSuite();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
}
