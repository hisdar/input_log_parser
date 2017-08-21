
// HisdarInstallerDlg.h : 头文件
//

#pragma once
#include "afxwin.h"
#include "installdlg.h"


// CHisdarInstallerDlg 对话框
class CHisdarInstallerDlg : public CDialogEx
{
// 构造
public:
	CHisdarInstallerDlg(CWnd* pParent = NULL);	// 标准构造函数

// 对话框数据
	enum { IDD = IDD_HISDARINSTALLER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV 支持


// 实现
protected:
	HICON m_hIcon;

	// 生成的消息映射函数
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
	CEdit installPathEdit;
	CString installPath;
	afx_msg void OnBnClickedButtonInstallPath();
	afx_msg void OnBnClickedButtonInstall();
	CInstallDlg installDlg;

	BOOL ReleaseResource( CString strFileName, WORD wResID, CString strFileType );
	bool UnZipPachage(CString srcZipFile, CString unZipFolder);
	bool CopyInstallFile(CString srcFolder, CString tagFolder);
};
