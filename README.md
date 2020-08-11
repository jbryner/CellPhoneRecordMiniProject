CellPhoneRecordMiniProject

To build the jar file:
./gradlew clean build jar

To run the program:
java -jar build/libs/CellPhoneRecordMiniProject-1.0.jar [yyyy]

If you do not specify the year, it will default to printing all years.

This defaults to loading the files CellPhone.csv and CellPhoneUsageByMonth.csv
You can specify other csv files by 
java -jar build/libs/CellPhoneRecordMiniProject-1.0.jar [yyyy] [alternate CellPhone.csv] [alternate CellPhoneUsageByMonth.csv]

Features:
It prints the output to local printer.
It prints a summary on the first page.
It separates the output by customerId and sorts the output by bill date.
It prints the customer id, name, phone model and purchase date only once.


Considerations:

This handles the data in memory. For large datasets, it would need to be written to store it in a relational database.
