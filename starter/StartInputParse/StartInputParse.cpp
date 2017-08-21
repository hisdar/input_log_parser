// StartInputParse.cpp : ����Ӧ�ó������ڵ㡣
//

#include "stdafx.h"
#include "StartInputParse.h"
#include "PathTool.h"
#include <shellapi.h>
#include <stdio.h>

#define JAVA_PATH _T(".\\Jre\\bin\\java.exe")
#define INPUT_PARSE_FILE _T("InputEventParse.jar")
#define PROGRAM_CLASS_PATH _T(".\\Adb\\")

int APIENTRY _tWinMain(HINSTANCE hInstance,
                        HINSTANCE hPrevInstance,
                     LPTSTR    lpCmdLine,
                     int       nCmdShow)
{
	CString runTimeFolder;
	PathTool pathTool;
	BOOL bRetVal = FALSE;

	// get program run folder
	bRetVal = pathTool.GetRunTimeFolder(runTimeFolder);
	if (bRetVal != TRUE)
	{
		MessageBox(NULL, _T("��Ǹ����ȡ���л��������볢����������!"), _T("������Ϣ"), MB_OK | MB_ICONERROR);
		return 1;
	}

	// set work path
	SetCurrentDirectory(runTimeFolder);

	// set adb class path
	CString adbPath = runTimeFolder;
	adbPath.Append(PROGRAM_CLASS_PATH);
	bRetVal = pathTool.IsPathInSystemEnvironmentVariable(adbPath);
	if (bRetVal == FALSE)
	{
		pathTool.AddPathToEnvironmentVariable(adbPath);
	}

	CString cmdString = JAVA_PATH;
	cmdString.Append(_T(" -splash:splash:../../Image/welcomeImage.png -jar InputEventParse.jar"));

	WinExec(cmdString, SW_HIDE);

	return 1;
}