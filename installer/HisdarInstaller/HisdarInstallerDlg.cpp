
// HisdarInstallerDlg.cpp : ʵ���ļ�
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


// ����Ӧ�ó��򡰹��ڡ��˵���� CAboutDlg �Ի���

class CAboutDlg : public CDialogEx
{
public:
	CAboutDlg();

// �Ի�������
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV ֧��

// ʵ��
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


// CHisdarInstallerDlg �Ի���




CHisdarInstallerDlg::CHisdarInstallerDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CHisdarInstallerDlg::IDD, pParent)
	, installPath(_T(""))
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);

	// ��װӦ�ó���װ·��
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


// CHisdarInstallerDlg ��Ϣ�������

BOOL CHisdarInstallerDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// ��������...���˵�����ӵ�ϵͳ�˵��С�

	// IDM_ABOUTBOX ������ϵͳ���Χ�ڡ�
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

	// ���ô˶Ի����ͼ�ꡣ��Ӧ�ó��������ڲ��ǶԻ���ʱ����ܽ��Զ�
	//  ִ�д˲���
	SetIcon(m_hIcon, TRUE);			// ���ô�ͼ��
	SetIcon(m_hIcon, FALSE);		// ����Сͼ��

	// TODO: �ڴ���Ӷ���ĳ�ʼ������
	installPathEdit.SetWindowTextW(installPath);

	// ���öԻ������
	SetWindowText(INSTALL_PROGRAM_NAME);

	// ������װ�Ի���
	installDlg.Create(IDD_INSTALL_DIALOG, this);

	return TRUE;  // ���ǽ��������õ��ؼ������򷵻� TRUE
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

// �����Ի��������С����ť������Ҫ����Ĵ���
//  �����Ƹ�ͼ�ꡣ����ʹ���ĵ�/��ͼģ�͵� MFC Ӧ�ó���
//  �⽫�ɿ���Զ���ɡ�

void CHisdarInstallerDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // ���ڻ��Ƶ��豸������

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// ʹͼ���ڹ����������о���
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// ����ͼ��
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

//���û��϶���С������ʱϵͳ���ô˺���ȡ�ù��
//��ʾ��
HCURSOR CHisdarInstallerDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}



void CHisdarInstallerDlg::OnBnClickedButtonInstallPath()
{
	PathTool pathTool;

	// ���жϰ�װĿ¼�Ƿ���ڣ���������ڣ��ȴ�����װ��Ŀ¼
	if (!pathTool.IsFolderExist(DEFAULT_INSTALL_PATH)) 
	{
		pathTool.CreateFolderEx(DEFAULT_INSTALL_PATH);
	}

	// �����ļ���ѡ��Ի���
	CFolderPickerDialog installPathChoicer(DEFAULT_INSTALL_PATH);
	int retVal = installPathChoicer.DoModal();
	if (retVal != IDOK)
	{
		return;
	}

	// ��ȡѡ���Ŀ¼
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
	
	// ��ѡ���Ŀ¼���õ���������
	installPathEdit.SetWindowTextW(installPath);

	// ���ѡ���Ŀ¼����Ĭ�ϵİ�װĿ¼��ɾ��һ��ʼ�����İ�װĿ¼
	if (installPath.Find(DEFAULT_INSTALL_PATH) < 0)
	{
		pathTool.DeleteFolderEx(DEFAULT_INSTALL_PATH);
	}
}


void CHisdarInstallerDlg::OnBnClickedButtonInstall()
{
	int retVal = 0;

	// ��ȡ�û����õİ�װĿ¼
	CString userInstallPath;
	installPathEdit.GetWindowTextW(userInstallPath);
	TRACE(_T("User install path:%s\n"), userInstallPath);

	PathTool pathTool;

	// ���ж��û�ѡ���Ŀ¼�Ƿ����
	if (pathTool.IsFolderExist(userInstallPath))
	{
		// ���Ŀ¼�Ѿ����ڣ���ʾ�û��Ƿ񸲸�
		retVal = MessageBox(
			_T("��װĿ¼�Ѵ��ڣ��Ƿ񸲸ǣ�\n������븲�ǣ�������ѡ��װ��Ŀ¼��\n[����]����Ŀ¼���ܴ�ŵ����ϵİ�װ�汾������ֱ�Ӹ��ǡ�"), 
			_T("��ʾ"), 
			MB_YESNO | MB_ICONWARNING);

		if (retVal != IDYES) {
			return;
		}

		// ɾ�����еİ�װ��Ŀ¼
		TRACE(_T("Delete path:%s\n"), userInstallPath);
		pathTool.DeleteFolderEx(userInstallPath);
	}

	installPath = userInstallPath;

	// ��ʾ��װ�Ի���
	CRect windowRect;
	GetWindowRect(windowRect);

	//installDlg.getwind
	installDlg.SetWindowTextW(INSTALL_PROGRAM_NAME);
	installDlg.MoveWindow(windowRect);
	installDlg.ShowWindow(SW_SHOW);

	ShowWindow(SW_HIDE);

	// ������װ�̰߳�װ
	installDlg.OutputLine(_T("������װ����"));
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
	logString.Format(_T("�ͷŰ�װ����dwSize:%d"), dwSize);
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

	// ��ȡ�û����õİ�װĿ¼
	CString installPath;
	mainDlg->installPathEdit.GetWindowTextW(installPath);

	// ���ó���װ�ı�־
	installDlg->installFlag = true;

	// ɾ����װĿ¼
	pathTool.DeleteFolderEx(installPath);

	logString.SetString(_T("������װĿ¼��"));
	logString.Append(installPath);
	installDlg->OutputLine(logString);

	bRetVal = pathTool.CreateFolderEx(installPath);
	if (!bRetVal)
	{
		MessageBox(installDlg->GetSafeHwnd(), 
					_T("������װĿ¼ʧ�ܣ���ȷ�ϰ�װĿ¼�Ƿ�Ϸ���"), 
					_T("����"), 
					MB_OK | MB_ICONERROR);

		mainDlg->ShowWindow(SW_SHOW);
		installDlg->ShowWindow(SW_HIDE);

		// ���ó���װ�ı�־
		installDlg->installFlag = false;
		return 0;
	}

	// ��鰲װĿ¼�Ƿ񴴽��ɹ�
	if (!pathTool.IsFolderExist(installPath)) 
	{
		MessageBox(installDlg->GetSafeHwnd(), 
			_T("��װĿ¼����ʧ�ܣ��޷�������װ��"), 
			_T("����"), 
			MB_OK | MB_ICONERROR);
		// ���ó���װ�ı�־
		installDlg->installFlag = false;

		return 0;
	}

	// ��ȡϵͳ��ʱ�ļ���·��
	_TCHAR readBuffer[MAX_READ_BUFFER_LENGTH];
	int pathLength = GetTempPath(MAX_READ_BUFFER_LENGTH - 1, readBuffer);
	if (pathLength <= 0)
	{
		TRACE(_T("%d:Get Temp Path fail\n"), __LINE__);
		MessageBox(installDlg->GetSafeHwnd(), 
			_T("��ȡϵͳ��ʱ�ļ���ʧ�ܣ��޷���װ��"), 
			_T("����"), 
			MB_OK | MB_ICONERROR);

		// ���ó���װ�ı�־
		installDlg->installFlag = false;

		return 0;
	}

	TRACE(_T("%d:Temp Path is %s\n"), __LINE__, readBuffer);

	CString tempPath;
	tempPath.Format(_T("%s"), readBuffer);

	// �����ʱĿ¼�Ƿ���ڣ���������ڣ�������ʱĿ¼
	if (!pathTool.IsFolderExist(tempPath)) 
	{
		if (pathTool.CreateFolderEx(tempPath)) 
		{
			MessageBox(installDlg->GetSafeHwnd(), 
				_T("����ϵͳ��ʱ�ļ���ʧ�ܣ��޷�������װ��"), 
				_T("����"), 
				MB_OK | MB_ICONERROR);
			// ���ó���װ�ı�־
			installDlg->installFlag = false;

			return 0;
		}
	}

	// �ͷŰ�װ��
	CString packagePath;
	packagePath = pathTool.PathCat(tempPath, PROGRAM_NAME);
	packagePath.Append(_T(".zip"));

	// ��黺���ļ����Ƿ���ڣ���������ڵĻ��ȴ���
	if (!pathTool.IsFolderExist(tempPath))
	{
		pathTool.CreateFolderEx(tempPath);
	}

	// �ͷŰ�װ��
	bRetVal = mainDlg->ReleaseResource(packagePath, IDR_PACKAGE_PROGRAM, _T("PACKAGE"));
	if (!bRetVal)
	{
		MessageBox(installDlg->GetSafeHwnd(), 
			_T("�ͷŰ�װ��ʧ�ܣ�����װʧ�ܣ�"), 
			_T("����"), 
			MB_OK | MB_ICONERROR);
		// ���ó���װ�ı�־
		installDlg->installFlag = false;
		return 0;
	}

	CString packageTempPath = tempPath + PROGRAM_NAME;
	
	// ��ѹ��װ��
	mainDlg->UnZipPachage(packagePath, packageTempPath);

	// ������װ�ļ�
	mainDlg->CopyInstallFile(packageTempPath, mainDlg->installPath);

	// ������ݷ�ʽ
	CString startFile = _T("\\StartInputParse.exe");
	startFile = pathTool.PathCat(installPath, startFile);

	installDlg->OutputLine(_T("������ݷ�ʽ\r\n"));
	pathTool.CreateDesktopShotCut(_T("�������"), startFile);

	// ɾ�������ļ�
	logString.Format(_T("ɾ����ʱ�ļ���%s"), packageTempPath);
	installDlg->OutputLine(logString);
	pathTool.DeleteFolderEx(packageTempPath);
	
	logString.Format(_T("ɾ����ʱ�ļ���%s"), packagePath);
	installDlg->OutputLine(logString);
	DeleteFile(packagePath);

	installDlg->OutputLine(_T("��װ���"));
	
	installDlg->EnableFinishButton(true);

	// ���ó���װ�ı�־
	installDlg->installFlag = false;
	return 0;
}

bool CHisdarInstallerDlg::UnZipPachage(CString srcZipFile, CString unZipFolder)
{
	
	PathTool pathTool;
	CString logString;

	// �������Ŀ¼
	if (pathTool.IsFolderExist(unZipFolder))
	{
		pathTool.DeleteFolderEx(unZipFolder);
	}

	logString.Format(_T("���������ʱ���Ŀ¼��%s"), unZipFolder);
	installDlg.OutputLine(logString);

	pathTool.CreateFolderEx(unZipFolder);

	HZIP hz = OpenZip(srcZipFile, 0);
	ZIPENTRY ze;
	DWORD zResult = ZR_OK;

	GetZipItem(hz, -1, &ze); 
	int fileCount = ze.index;
	
	logString.Format(_T("�ҵ��ļ���%d��"), fileCount);
	installDlg.OutputLine(logString);

	installDlg.SetProgressRange(0, fileCount);
	installDlg.SetProcessValue(0);
	for (int zi = 0; zi < fileCount; zi++)
	{ 
		ZIPENTRY ze;
		GetZipItem(hz,zi,&ze); 

		CString unZipFile = pathTool.PathCat(unZipFolder, ze.name);
		unZipFile.Replace('/', '\\');

		logString.Format(_T("��ѹ:%s"), unZipFile);
		installDlg.OutputLine(logString);

		zResult = UnzipItem(hz, zi, unZipFile); 
		if (zResult != ZR_OK)
		{
			// un zip fail
			if (pathTool.IsFolderExist(unZipFile))	
			{
				logString.Format(_T("��ѹ����:%s"), unZipFile);
				installDlg.OutputLine(logString);
				return false;
			}
		}

		installDlg.SetProcessValue(zi + 1);
	}

	installDlg.OutputLine(_T("��ѹ���"));
	return true;
}

bool CHisdarInstallerDlg::CopyInstallFile(CString srcFolder, CString tagFolder)
{
	PathTool pathTool;
	CString logString;

	// ������װĿ¼
	logString.Format(_T("������װĿ¼��%s"), tagFolder);
	installDlg.OutputLine(logString);
	if (pathTool.IsFolderExist(tagFolder))
	{
		pathTool.DeleteFolderEx(tagFolder);
	}

	pathTool.CreateFolderEx(tagFolder);

	// ��ȡ����Ŀ¼�µ��ļ��б�
	CArray<CString> fileList;
	int fileCount = pathTool.GetFileList(srcFolder, fileList);

	installDlg.SetProgressRange(0, fileCount);
	installDlg.SetProcessValue(0);
	for (int i = 0; i < fileCount; i++)
	{
		CString tagFilePath = fileList.GetAt(i);
		tagFilePath = tagFilePath.Right(tagFilePath.GetLength() - srcFolder.GetLength());

		tagFilePath = pathTool.PathCat(installPath, tagFilePath);

		logString.Format(_T("�����ļ���:%s"), tagFilePath);
		installDlg.OutputLine(logString);

		pathTool.CopyFileEx(fileList.GetAt(i), tagFilePath);	
		installDlg.SetProcessValue(i + 1);
	}

	logString.Format(_T("��� %d ���ļ�����"), fileCount);
	installDlg.OutputLine(logString);

	return true;
}
