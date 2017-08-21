
// HisdarInstallerDlg.h : ͷ�ļ�
//

#pragma once
#include "afxwin.h"
#include "installdlg.h"


// CHisdarInstallerDlg �Ի���
class CHisdarInstallerDlg : public CDialogEx
{
// ����
public:
	CHisdarInstallerDlg(CWnd* pParent = NULL);	// ��׼���캯��

// �Ի�������
	enum { IDD = IDD_HISDARINSTALLER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV ֧��


// ʵ��
protected:
	HICON m_hIcon;

	// ���ɵ���Ϣӳ�亯��
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
