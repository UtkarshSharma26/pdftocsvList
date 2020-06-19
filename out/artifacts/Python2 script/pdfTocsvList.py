import PyPDF2
import csv
import os
import re

currentPath = os.path.dirname(os.path.abspath(__file__))
keywordsPath = currentPath + "/keywords.txt"
fileListPath = currentPath + "/FileList.csv"
errorFilePath = currentPath + "/errorFiles.txt"


def deleteFile(filePath):
    if os.path.exists(filePath):
        os.remove(filePath)


def getAllPdfFiles(path):
    pdfList = []
    for root, dirs, files in os.walk(path):
        for file in files:
            if (file.endswith(".pdf")):
                pdfList.append(os.path.join(root, file))
    return pdfList


def setHeaderDict(keywords):
    headerDict = {}
    headerDict["File Path"] = "File Path"
    headerDict["File Name"] = "File Name"
    headerDict["File Size"] = "File Size"
    headerDict["No. of Pages"] = "No. of Pages"
    headerDict["Status"] = "Status"
    for keyword in keywords:
        headerDict[keyword] = keyword
    return headerDict


def setDetailsDict(pdfPath, keywords, pdf):
    detailsDict = {}
    detailsDict["File Path"] = pdfPath
    detailsDict["File Name"] = pdfPath.rsplit('/', 1)[1]
    detailsDict["File Size"] = os.stat(pdfPath).st_size
    detailsDict["No. of Pages"] = pdf.numPages
    detailsDict["Status"] = "Active"
    for key in keywords:
        detailsDict[key] = ""
    return detailsDict


def errorDict(pdfPath, keywords):
    detailsDict = {}
    detailsDict["File Path"] = pdfPath
    detailsDict["File Name"] = pdfPath.rsplit('/', 1)[1]
    detailsDict["File Size"] = os.stat(pdfPath).st_size
    detailsDict["No. of Pages"] = ""
    detailsDict["Status"] = "Error"
    for key in keywords:
        detailsDict[key] = ""
    return detailsDict


def dictToCsv(my_dict):
    with open(fileListPath, 'a') as f:
        w = csv.DictWriter(f, my_dict.keys())
        w.writerow(my_dict)


def writeTxtFile(text):
    hs = open(errorFilePath, 'a')
    hs.write(text + "\n")
    hs.close()


def mainFn():
    deleteFile(errorFilePath)
    deleteFile(fileListPath)

    try:
        with open(keywordsPath, "r") as fd:
            keywords = fd.read().splitlines()
    except IOError:
        keywords = []

    pdfFiles = getAllPdfFiles(currentPath)

    headerDict = setHeaderDict(keywords)
    dictToCsv(headerDict)

    for pdfPath in pdfFiles:
        try:
            pdfFileObj = open(pdfPath, 'rb')
            pdf = PyPDF2.PdfFileReader(pdfFileObj, strict=False)
            detailsDict = setDetailsDict(pdfPath, keywords, pdf)
            if pdf.numPages == 1 and len(keywords) > 0:
                pageObj = pdf.getPage(0)
                text = pageObj.extractText()
                for key in keywords:
                    exists = re.search(key, text)
                    if (exists != None):
                        detailsDict[key] = "true"
                    else:
                        detailsDict[key] = "false"
            dictToCsv(detailsDict)
            pdfFileObj.close()
        except PyPDF2.utils.PdfReadError:
            dictToCsv(errorDict(pdfPath, keywords))
            writeTxtFile(pdfPath)


mainFn()
