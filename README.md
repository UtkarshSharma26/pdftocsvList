#### **This is a small utility which reads pdf files from a folder and writes the following fields in a CSV.**
1. No. of Pages
2. File Name
3. File Path
4. Status
5. File Size
6. Keywords
4. If pages are less than 2 then it reads keys from Keywords.txt file and checks if those words exists in pdf and populate the csv.


###### **Steps Java:**
JAR could be found at out/artifacts/PdfReaderUtility_jar/PdfReaderUtility.jar
1. Execute the jar file in the folder from where you want the program to pick up folders to check.
2. Keep the Keywords.txt file in the same folder structure.
3. FileList.csv file will be your output file with consolidated error and healthy files, whereas, errorFiles.txt contains all error files path.


###### **Steps Python:**
Python script could be found at out/artifacts/Python3 script/pdfTocsvList.py
1. Make sure you have PyPDF2 installed,if not execute `pip3 install PyPDF2`
2. Execute the script file in the folder from where you want the program to pick up folders to check.
3. Keep the Keywords.txt file in the same folder structure.
4. FileList.csv file will be your output file with consolidated error and healthy files, whereas, errorFiles.txt contains all error files path.

