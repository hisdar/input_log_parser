#include "StdAfx.h"
#include "PathTool.h"


PathTool::PathTool(void)
{
}


PathTool::~PathTool(void)
{
}

BOOL PathTool::IsFolderExist(CString path)
{
	if (path.GetLength() == 0)
	{
		return FALSE;
	}

	path.Replace('/', '\\');
	if (path.Right(1).Compare(_T("\\")) != 0)
	{
		path.Append(_T("\\"));
	}

	path.Append(_T("*.*"));

	CFileFind fileFinder;
	BOOL bWorking = fileFinder.FindFile(path);
	fileFinder.Close();
	if (bWorking)
	{
		return TRUE;
	}

	return FALSE;
}

BOOL PathTool::CopyFileEx(CString srcPath, CString tagPath)
{
	CString tagFatherPath = GetParentPath(tagPath);

	CreateFolderEx(tagFatherPath);
	CopyFile(srcPath, tagPath, TRUE);

	return 0;
}


BOOL PathTool::CreateFolderEx(CString path)
{
	path.Replace('/', '\\');
	if (path.ReverseFind('\\') != path.GetLength() - 1)
	{
		path.Append(_T("\\"));
	}

	if (IsFolderExist(path))
	{
		return TRUE;
	}

	// get father folder
	CString fatherPath = GetParentPath(path);

	// if father folder is not exist, create father folder
	if (!IsFolderExist(fatherPath))
	{
		CreateFolderEx(fatherPath);
	}

	CreateDirectory(path, NULL);

	return TRUE;
}

BOOL PathTool::DeleteFolderEx(CString path)
{
	BOOL bRetVal = FALSE;

	bRetVal = IsFolderExist(path);
	if (bRetVal == FALSE) {
		return TRUE;
	}

	CString pathSerch = path;
	pathSerch = PathCat(path, _T("*.*"));

	CFileFind fileFinder;
	bRetVal = fileFinder.FindFile(pathSerch);
	if (!bRetVal) {
		TRACE(_T("Fail to open:%s, %d\n"), path, GetLastError());
		return TRUE;
	}

	bRetVal = fileFinder.FindNextFile();
	while (bRetVal) {
		TRACE(_T("Path:%s\n"), fileFinder.GetFilePath());
		// 如果这是个目录，继续调用删除目录的功能
		if (fileFinder.IsDirectory()) {
			if (!fileFinder.IsDots()) {
				TRACE(_T("Delete Path:%s\n"), fileFinder.GetFilePath());
				DeleteFolderEx(fileFinder.GetFilePath());
			}
			
		} else {
			// 删除这个文件
			TRACE(_T("Delete:%s\n"), fileFinder.GetFilePath());
			DeleteFile(fileFinder.GetFilePath());
		}

		bRetVal = fileFinder.FindNextFile();
	}

	TRACE(_T("Remove:%s\n"), path);
	RemoveDirectory(path);

	fileFinder.Close();

	return TRUE;
}

BOOL PathTool::CopyFolderEx(CString srcPath, CString tagPath)
{
	BOOL bRetVal = FALSE;

	bRetVal = IsFolderExist(srcPath);
	if (bRetVal == FALSE)
	{
		return FALSE;
	}

	bRetVal = IsFolderExist(tagPath);
	if (bRetVal == FALSE)
	{
		bRetVal = CreateFolderEx(tagPath);
		if (bRetVal == FALSE)
		{
			return FALSE;
		}
	}

	return TRUE;
}

BOOL PathTool::IsFolder(CString path)
{

	return TRUE;
}

BOOL PathTool::IsFile(CString path)
{
	return TRUE;
}

CString PathTool::GetParentPath(CString path)
{
	path.Replace('/', '\\');
	if (path.ReverseFind('\\') == path.GetLength() - 1)
	{
		path = path.Left(path.GetLength() - 1);
	}

	int index = path.ReverseFind('\\');
	if (index < 0)
	{
		return _T("");
	}

	path = path.Left(index);

	return path;
}

CString PathTool::PathCat(CString fatherPath, CString childPath)
{
	fatherPath.Replace('/', '\\');
	childPath.Replace('/', '\\');

	if (fatherPath.ReverseFind('\\') != fatherPath.GetLength() - 1)
	{
		fatherPath.Append(_T("\\"));
	}

	if (childPath.Find('\\') == 0)
	{
		childPath = childPath.Right(childPath.GetLength() - 1);
	}

	fatherPath.Append(childPath);

	return fatherPath;
}

BOOL PathTool::GetRunTimeFolder(CString &runTimeFolder) 
{
	TCHAR szFilePath[MAX_PATH + 1];
	DWORD dRetVal = 0;

	dRetVal = GetModuleFileName(NULL, szFilePath, MAX_PATH);
	if (dRetVal == 0) {
		return FALSE;
	}

	runTimeFolder.SetString(szFilePath);
	runTimeFolder = GetParentPath(runTimeFolder);

	return TRUE;
}

BOOL PathTool::GetSystemEnvironmentVariable(CString &sysEnvVar)
{
	TCHAR sysEnvVar_[MAX_BUFFER_LENGTH];
	DWORD dRetVal = 0;

	dRetVal = GetEnvironmentVariable(_T("PATH"), sysEnvVar_, MAX_BUFFER_LENGTH - 1);
	if (dRetVal == 0)
	{
		return FALSE;
	}

	sysEnvVar.Format(_T("%s"), sysEnvVar_);
	return TRUE;
}

BOOL PathTool::IsPathInSystemEnvironmentVariable(CString path) 
{
	int findIndex = 0;
	BOOL bRetVal = FALSE;
	CString sysEnvVar;

	bRetVal = GetSystemEnvironmentVariable(sysEnvVar);
	if (bRetVal != TRUE)
	{
		return FALSE;
	}

	// search path in system environment variable
	findIndex = sysEnvVar.Find(path);
	if (findIndex < 0) 
	{
		return FALSE;
	}

	return TRUE;
}

BOOL PathTool::AddPathToEnvironmentVariable(CString path) 
{
	BOOL bRetVal = FALSE;
	CString sysEnvVar;

	bRetVal = GetSystemEnvironmentVariable(sysEnvVar);
	if (bRetVal != TRUE)
	{
		return FALSE;
	}

	sysEnvVar.Append(_T(";"));
	sysEnvVar.Append(path);

	bRetVal = SetEnvironmentVariable(_T("PATH"), sysEnvVar);

	return bRetVal;
}

int PathTool::GetFileList(CString path, CArray<CString> & fileList)
{
	int fileCount = 0;

	BOOL retVal = IsFolderExist(path);
	if (retVal != TRUE)
	{
		TRACE(_T("%d:Path %s is not exist"), __LINE__, path);
		return fileCount;
	}

	path.Replace('/', '\\');
	if (path.Right(1).Compare(_T("\\")) != 0)
	{
		path.Append(_T("\\"));
	}

	path.Append(_T("*.*"));

	CFileFind fileFinder;
	retVal = fileFinder.FindFile(path);
	if (retVal == FALSE)
	{
		TRACE(_T("%d:find %s fail\n"), __LINE__, path);
		return fileCount;
	}

	retVal = fileFinder.FindNextFile();
	while (retVal) 
	{
		if (fileFinder.IsDots())
		{
			retVal = fileFinder.FindNextFile();
			continue;
		}

		// if this is a folder
		if (fileFinder.IsDirectory())
		{
			fileCount += GetFileList(fileFinder.GetFilePath(), fileList);
		}
		else
		{
			fileList.Add(fileFinder.GetFilePath());
			fileCount += 1;
		}

		retVal = fileFinder.FindNextFile();
	}

	if (fileFinder.IsDots())
	{
		retVal = fileFinder.FindNextFile();
	}
	else if (fileFinder.IsDirectory())
	{
		// if this is a folder
		fileCount += GetFileList(fileFinder.GetFilePath(), fileList);
	}
	else
	{
		fileList.Add(fileFinder.GetFilePath());
		fileCount += 1;
	}

	return fileCount;
}

BOOL PathTool::CreateDesktopShotCut( CString strName, CString strSourcePath )
{
	// Initializes the COM library on the current thread and identifies the 
	// concurrency model as single-thread apartment (STA).
	if(FAILED(CoInitialize(NULL)))
	{
		return FALSE;
	}

	HRESULT hResult = S_FALSE;
	BOOL bRetVal = FALSE;

	LPMALLOC pShell = NULL;
	hResult = SHGetMalloc(&pShell);
	if (hResult != S_OK) {
		CoUninitialize();
		return FALSE;
	}

	LPITEMIDLIST pidl = NULL;
	int nFolder = CSIDL_DESKTOPDIRECTORY;
	// Retrieves a pointer to the ITEMIDLIST structure of a special folder.
	hResult = SHGetSpecialFolderLocation(NULL, nFolder, &pidl);
	if (hResult != S_OK) {
		CoUninitialize();
		return FALSE;
	}

	TCHAR path[MAX_PATH + 1];
	// Converts an item identifier list to a file system path.
	bRetVal = SHGetPathFromIDList(pidl, path);
	if(bRetVal != TRUE)
	{
		pShell->Free(pidl);
		CoUninitialize();
		return FALSE;
	}
	pShell->Release();

	IShellLink* psl = NULL;
	CString strDestDir;
	strDestDir.Format(_T("%s\\%s.lnk"), path, strName); // name of desktop shortcut 
	hResult = CoCreateInstance(CLSID_ShellLink, NULL, CLSCTX_INPROC_SERVER, IID_IShellLink, (LPVOID*)&psl);
	if(hResult != S_OK) 
	{
		CoUninitialize();
		return FALSE;
	}

	// set the path of desktop shortcut, the path is not the file path,
	// this path is the file while it's folder,
	// such as the path of C:\\windows\\a.txt is C:\\windows\\ 
	psl->SetPath(strSourcePath);
	psl->SetWorkingDirectory(strSourcePath);   //set shortcut start path

	IPersistFile* ppf = NULL;
	hResult = psl->QueryInterface(IID_IPersistFile,(LPVOID*)&ppf);
	if(hResult < 0)
	{
		ppf->Release();
		psl->Release();
		CoUninitialize();
		return FALSE;
	}

#ifndef UNICODE
	WCHAR wsz[MAX_PATH] = {0};
	MultiByteToWideChar(CP_THREAD_ACP, MB_PRECOMPOSED, strDestDir, -1,wsz, MAX_PATH);//
	hResult = ppf->Save(wsz, TRUE);//save desktop shortcut
#else
	hResult = ppf->Save(strDestDir, TRUE);//save desktop shortcut
#endif

	ppf->Release();
	psl->Release();
	CoUninitialize();

	if(hResult < 0)
	{
		return FALSE;
	}
	else
	{
		return TRUE;
	}
}

