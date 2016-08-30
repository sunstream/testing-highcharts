TASK:

1. Take up to 3 days to create an automated test based on Selenium 2 for the 1st chart on this page: 
http://www.highcharts.com/component/content/article/2-news/146-highcharts-5th-anniversary

2. Extract values for the green area chart called 'Highsoft employees' and verify them against values provided in the test program

3. Provide the test code and necessary documentation on how to install and run the test using maven or similar framework 


USAGE:

1. Setup Java. To use the framework, you will need JDK 8.0. Download the installer from official site: https://java.com/ru/download/
2. Setup Maven. To set up Maven, please use the official instruction: http://www.tutorialspoint.com/maven/maven_environment_setup.htm
3. Setup project. Unzip the project to the folder which will be further referred to as #{project dir}. Execute shell script compile.bat (for Windows) or compile.sh (for Linux).
4. Launch the tests. Execute shell script run.bat (for Windows) or run.sh (for Linux).
5. Examine reports. To see the latest report, open file #{project dir}/target/surefire-reports/index.html.
6. Extend framework. 
To add a new test class, you need to: 
  * create a new class extending BaseTest;
  * add new test data into data.properties file or add a separate file with data and specify its name in your new class;
  * add your new class name into testng.xml into <classes> section.


