#pragma once

#define MAX_BUFFER_LENGTH (1024 * 2)

class PathTool
{
public:
	PathTool(void);
	~PathTool(void);

	BOOL IsFolderExist(CString path);
	BOOL IsFolder(CString path);
	BOOL IsFile(CString path);

	BOOL CopyFileEx(CString srcPath, CString tagPath);
	BOOL CopyFolderEx(CString srcPath, CString tagPath);
	BOOL CreateFolderEx(CString path);
	BOOL DeleteFolderEx(CString path);

	CString GetParentPath(CString path);
	CString PathCat(CString fatherPath, CString childPath);

	BOOL GetRunTimeFolder(CString &runTimeFolder);
	BOOL GetSystemEnvironmentVariable(CString &sysEnvVar);
	BOOL IsPathInSystemEnvironmentVariable(CString path);
	BOOL AddPathToEnvironmentVariable(CString path);


	BOOL CreateDesktopShotCut(CString strName, CString strSourcePath);

	int GetFileList(CString path, CArray<CString> & fileList);
};

