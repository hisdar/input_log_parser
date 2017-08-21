
// HisdarInstallerDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "HisdarInstaller.h"
#include "HisdarInstallerDlg.h"
#include "afxdialogex.h"
#include "InstallConfig.h"
#include "unzip.h"
#include "PathTool.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif

#define MAX_READ_BUFFER_LENGTH 1024

DWORD WINAPI InstallThreadFunc(LPVOID lpParam);


// 用于应用程序“关于”菜单项的 CAboutDlg 对话框

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// 对话框数据
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

// 实现
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialogEx(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialogEx)
END_MESSAGE_MAP()


// CHisdarInstallerDlg 对话框




CHisdarInstallerDlg::CHisdarInstallerDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CHisdarInstallerDlg::IDD, pParent)
	, installPath(_T(""))
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);

	// 组装应用程序安装路径
	PathTool pathTool;
	installPath = pathTool.PathCat(DEFAULT_INSTALL_PATH, PROGRAM_NAME);
}

void CHisdarInstallerDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_EDIT_INSTALL_PATH, installPathEdit);
}

BEGIN_MESSAGE_MAP(CHisdarInstallerDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_BN_CLICKED(IDC_BUTTON_INSTALL_PATH, &CHisdarInstallerDlg::OnBnClickedButtonInstallPath)
	ON_BN_CLICKED(IDC_BUTTON_INSTALL, &CHisdarInstallerDlg::OnBnClickedButtonInstall)
END_MESSAGE_MAP()


// CHisdarInstallerDlg 消息处理程序

BOOL CHisdarInstallerDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// 将“关于...”菜单项添加到系统菜单中。

	// IDM_ABOUTBOX 必须在系统命令范围内。
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		BOOL bNameValid;
		CString strAboutMenu;
		bNameValid = strAboutMenu.LoadString(IDS_ABOUTBOX);
		ASSERT(bNameValid);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// 设置此对话框的图标。当应用程序主窗口不是对话框时，框架将自动
	//  执行此操作
	SetIcon(m_hIcon, TRUE);			// 设置大图标
	SetIcon(m_hIcon, FALSE);		// 设置小图标

	// TODO: 在此添加额外的初始化代码
	installPathEdit.SetWindowTextW(installPath);

	// 设置对话框标题
	SetWindowText(INSTALL_PROGRAM_NAME);

	// 创建安装对话框
	installDlg.Create(IDD_INSTALL_DIALOG, this);

	return TRUE;  // 除非将焦点设置到控件，否则返回 TRUE
}

void CHisdarInstallerDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialogEx::OnSysCommand(nID, lParam);
	}
}

// 如果向对话框添加最小化按钮，则需要下面的代码
//  来绘制该图标。对于使用文档/视图模型的 MFC 应用程序，
//  这将由框架自动完成。

void CHisdarInstallerDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 用于绘制的设备上下文

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 使图标在工作区矩形中居中
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 绘制图标
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

//当用户拖动最小化窗口时系统调用此函数取得光标
//显示。
HCURSOR CHisdarInstallerDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



void CHisdarInstallerDlg::OnBnClickedButtonInstallPath()
{
	PathTool pathTool;

	// 先判断安装目录是否存在，如果不存在，先创建安装，目录
	if (!pathTool.IsFolderExist(DEFAULT_INSTALL_PATH)) 
	{
		pathTool.CreateFolderEx(DEFAULT_INSTALL_PATH);
	}

	// 创建文件夹选择对话框
	CFolderPickerDialog installPathChoicer(DEFAULT_INSTALL_PATH);
	int retVal = installPathChoicer.DoModal();
	if (retVal != IDOK)
	{
		return;
	}

	// 获取选择的目录
	CString choicedInstallPath = installPathChoicer.GetPathName();
	TRACE(_T("Choice install path:%s\n"), choicedInstallPath);

	int startIndex = choicedInstallPath.Find(PROGRAM_NAME);
	if ( startIndex < 0)
	{
		installPath = pathTool.PathCat(choicedInstallPath, PROGRAM_NAME);
	} 
	else 
	{
		installPath = choicedInstallPath;
	}
	
	// 将选择的目录设置到到界面上
	installPathEdit.SetWindowTextW(installPath);

	// 如果选择的目录不是默认的安装目录，删除一开始创建的安装目录
	if (installPath.Find(DEFAULT_INSTALL_PATH) < 0)
	{
		pathTool.DeleteFolderEx(DEFAULT_INSTALL_PATH);
	}
}


void CHisdarInstallerDlg::OnBnClickedButtonInstall()
{
	int retVal = 0;

	// 获取用户设置的安装目录
	CString userInstallPath;
	installPathEdit.GetWindowTextW(userInstallPath);
	TRACE(_T("User install path:%s\n"), userInstallPath);

	PathTool pathTool;

	// 先判断用户选择的目录是否存在
	if (pathTool.IsFolderExist(userInstallPath))
	{
		// 如果目录已经存在，提示用户是否覆盖
		retVal = MessageBox(
			_T("安装目录已存在，是否覆盖？\n如果不想覆盖，请重新选择安装，目录！\n[建议]：该目录可能存放的是老的安装版本，建议直接覆盖。"), 
			_T("提示"), 
			MB_YESNO | MB_ICONWARNING);

		if (retVal != IDYES) {
			return;
		}

		// 删除已有的安装，目录
		TRACE(_T("Delete path:%s\n"), userInstallPath);
		pathTool.DeleteFolderEx(userInstallPath);
	}

	installPath = userInstallPath;

	// 显示安装对话框
	CRect windowRect;
	GetWindowRect(windowRect);

	//installDlg.getwind
	installDlg.SetWindowTextW(INSTALL_PROGRAM_NAME);
	installDlg.MoveWindow(windowRect);
	installDlg.ShowWindow(SW_SHOW);

	ShowWindow(SW_HIDE);

	// 启动安装线程安装
	installDlg.OutputLine(_T("启动安装服务"));
	HANDLE hThread = CreateThread(NULL, 0, InstallThreadFunc, this, 0, NULL);
}

BOOL CHisdarInstallerDlg::ReleaseResource( CString strFileName, WORD wResID, CString strFileType )
{
	// resource size   
	DWORD dwWrite = 0;

	// Create file
	HANDLE hFile = CreateFile( strFileName, GENERIC_WRITE, FILE_SHARE_WRITE, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL );   
	if ( hFile == INVALID_HANDLE_VALUE )   
	{   
		return FALSE;   
	}   

	// find resource, load resource and write resource to file
	HRSRC   hrsc = FindResource( NULL, MAKEINTRESOURCE(wResID), strFileType );   
	HGLOBAL hG = LoadResource( NULL, hrsc );   
	DWORD   dwSize = SizeofResource( NULL, hrsc );   

	CString logString;
	logString.Format(_T("释放安装包：dwSize:%d"), dwSize);
	installDlg.OutputLine(logString);

	WriteFile( hFile, hG, dwSize, &dwWrite, NULL );

	CloseHandle( hFile );   
	return TRUE;   
}

DWORD WINAPI InstallThreadFunc(LPVOID lpParam)
{
	CHisdarInstallerDlg * mainDlg = (CHisdarInstallerDlg *)lpParam;
	CInstallDlg * installDlg = &mainDlg->installDlg;
	CString logString;
	PathTool pathTool;
	BOOL bRetVal = FALSE;

	installDlg->EnableFinishButton(false);

	// 获取用户设置的安装目录
	CString installPath;
	mainDlg->installPathEdit.GetWindowTextW(installPath);

	// 设置程序安装的标志
	installDlg->installFlag = true;

	// 删除安装目录
	pathTool.DeleteFolderEx(installPath);

	logString.SetString(_T("创建安装目录："));
	logString.Append(installPath);
	installDlg->OutputLine(logString);

	bRetVal = pathTool.CreateFolderEx(installPath);
	if (!bRetVal)
	{
		MessageBox(installDlg->GetSafeHwnd(), 
					_T("创建安装目录失败，请确认安装目录是否合法！"), 
					_T("错误"), 
					MB_OK | MB_ICONERROR);

		mainDlg->ShowWindow(SW_SHOW);
		installDlg->ShowWindow(SW_HIDE);

		// 设置程序安装的标志
		installDlg->installFlag = false;
		return 0;
	}

	// 检查安装目录是否创建成功
	if (!pathTool.IsFolderExist(installPath)) 
	{
		MessageBox(installDlg->GetSafeHwnd(), 
			_T("安装目录创建失败，无法继续安装！"), 
			_T("错误"), 
			MB_OK | MB_ICONERROR);
		// 设置程序安装的标志
		installDlg->installFlag = false;

		return 0;
	}

	// 获取系统临时文件夹路径
	_TCHAR readBuffer[MAX_READ_BUFFER_LENGTH];
	int pathLength = GetTempPath(MAX_READ_BUFFER_LENGTH - 1, readBuffer);
	if (pathLength <= 0)
	{
		TRACE(_T("%d:Get Temp Path fail\n"), __LINE__);
		MessageBox(installDlg->GetSafeHwnd(), 
			_T("获取系统临时文件夹失败，无法安装！"), 
			_T("错误"), 
			MB_OK | MB_ICONERROR);

		// 设置程序安装的标志
		installDlg->installFlag = false;

		return 0;
	}

	TRACE(_T("%d:Temp Path is %s\n"), __LINE__, readBuffer);

	CString tempPath;
	tempPath.Format(_T("%s"), readBuffer);

	// 检查临时目录是否存在，如果不存在，创建临时目录
	if (!pathTool.IsFolderExist(tempPath)) 
	{
		if (pathTool.CreateFolderEx(tempPath)) 
		{
			MessageBox(installDlg->GetSafeHwnd(), 
				_T("创建系统临时文件夹失败，无法继续安装！"), 
				_T("错误"), 
				MB_OK | MB_ICONERROR);
			// 设置程序安装的标志
			installDlg->installFlag = false;

			return 0;
		}
	}

	// 释放安装包
	CString packagePath;
	packagePath = pathTool.PathCat(tempPath, PROGRAM_NAME);
	packagePath.Append(_T(".zip"));

	// 检查缓冲文件夹是否存在，如果不存在的话先创建
	if (!pathTool.IsFolderExist(tempPath))
	{
		pathTool.CreateFolderEx(tempPath);
	}

	// 释放安装包
	bRetVal = mainDlg->ReleaseResource(packagePath, IDR_PACKAGE_PROGRAM, _T("PACKAGE"));
	if (!bRetVal)
	{
		MessageBox(installDlg->GetSafeHwnd(), 
			_T("释放安装包失败，程序安装失败！"), 
			_T("错误"), 
			MB_OK | MB_ICONERROR);
		// 设置程序安装的标志
		installDlg->installFlag = false;
		return 0;
	}

	CString packageTempPath = tempPath + PROGRAM_NAME;
	
	// 解压安装包
	mainDlg->UnZipPachage(packagePath, packageTempPath);

	// 拷贝安装文件
	mainDlg->CopyInstallFile(packageTempPath, mainDlg->installPath);

	// 创建快捷方式
	CString startFile = _T("\\StartInputParse.exe");
	startFile = pathTool.PathCat(installPath, startFile);

	installDlg->OutputLine(_T("创建快捷方式\r\n"));
	pathTool.CreateDesktopShotCut(_T("报点解析"), startFile);

	// 删除缓存文件
	logString.Format(_T("删除临时文件：%s"), packageTempPath);
	installDlg->OutputLine(logString);
	pathTool.DeleteFolderEx(packageTempPath);
	
	logString.Format(_T("删除临时文件：%s"), packagePath);
	installDlg->OutputLine(logString);
	DeleteFile(packagePath);

	installDlg->OutputLine(_T("安装完成"));
	
	installDlg->EnableFinishButton(true);

	// 设置程序安装的标志
	installDlg->installFlag = false;
	return 0;
}

bool CHisdarInstallerDlg::UnZipPachage(CString srcZipFile, CString unZipFolder)
{
	
	PathTool pathTool;
	CString logString;

	// 创建输出目录
	if (pathTool.IsFolderExist(unZipFolder))
	{
		pathTool.DeleteFolderEx(unZipFolder);
	}

	logString.Format(_T("创建输出临时输出目录：%s"), unZipFolder);
	installDlg.OutputLine(logString);

	pathTool.CreateFolderEx(unZipFolder);

	HZIP hz = OpenZip(srcZipFile, 0);
	ZIPENTRY ze;
	DWORD zResult = ZR_OK;

	GetZipItem(hz, -1, &ze); 
	int fileCount = ze.index;
	
	logString.Format(_T("找到文件：%d个"), fileCount);
	installDlg.OutputLine(logString);

	installDlg.SetProgressRange(0, fileCount);
	installDlg.SetProcessValue(0);
	for (int zi = 0; zi < fileCount; zi++)
	{ 
		ZIPENTRY ze;
		GetZipItem(hz,zi,&ze); 

		CString unZipFile = pathTool.PathCat(unZipFolder, ze.name);
		unZipFile.Replace('/', '\\');

		logString.Format(_T("解压:%s"), unZipFile);
		installDlg.OutputLine(logString);

		zResult = UnzipItem(hz, zi, unZipFile); 
		if (zResult != ZR_OK)
		{
			// un zip fail
			if (pathTool.IsFolderExist(unZipFile))	
			{
				logString.Format(_T("解压错误:%s"), unZipFile);
				installDlg.OutputLine(logString);
				return false;
			}
		}

		installDlg.SetProcessValue(zi + 1);
	}

	installDlg.OutputLine(_T("解压完成"));
	return true;
}

bool CHisdarInstallerDlg::CopyInstallFile(CString srcFolder, CString tagFolder)
{
	PathTool pathTool;
	CString logString;

	// 创建安装目录
	logString.Format(_T("创建安装目录：%s"), tagFolder);
	installDlg.OutputLine(logString);
	if (pathTool.IsFolderExist(tagFolder))
	{
		pathTool.DeleteFolderEx(tagFolder);
	}

	pathTool.CreateFolderEx(tagFolder);

	// 获取缓存目录下的文件列表
	CArray<CString> fileList;
	int fileCount = pathTool.GetFileList(srcFolder, fileList);

	installDlg.SetProgressRange(0, fileCount);
	installDlg.SetProcessValue(0);
	for (int i = 0; i < fileCount; i++)
	{
		CString tagFilePath = fileList.GetAt(i);
		tagFilePath = tagFilePath.Right(tagFilePath.GetLength() - srcFolder.GetLength());

		tagFilePath = pathTool.PathCat(installPath, tagFilePath);

		logString.Format(_T("复制文件到:%s"), tagFilePath);
		installDlg.OutputLine(logString);

		pathTool.CopyFileEx(fileList.GetAt(i), tagFilePath);	
		installDlg.SetProcessValue(i + 1);
	}

	logString.Format(_T("完成 %d 个文件拷贝"), fileCount);
	installDlg.OutputLine(logString);

	return true;
}
