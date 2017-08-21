#pragma once
#include "afxcmn.h"
#include "afxwin.h"
#include "LogPool.h"

#define DEFAULT_LOG_BUFFER_LEN 50

// CInstallDlg 对话框

class CInstallDlg : public CDialogEx
{
	DECLARE_DYNAMIC(CInstallDlg)

public:
	CInstallDlg(CWnd* pParent = NULL);   // 标准构造函数
	virtual ~CInstallDlg();

// 对话框数据
	enum { IDD = IDD_INSTALL_DIALOG };

protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

	DECLARE_MESSAGE_MAP()
public:
	void SetProcessValue(int value);
	CProgressCtrl progressBar;
	CButton finishButton;
	void OutputLine(CString log);
	void SetProgressRange(int min, int max);
//	afx_msg void OnPaint();
	afx_msg void OnPaint();

	LogPool logPool;
	afx_msg void OnBnClickedButtonFinish();
	void EnableFinishButton(bool enable);
	void PaintLog(void);
	afx_msg void OnClose();
	bool installFlag;
};
