package com.pinhuba.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinhuba.common.email.MailReceiveBean;
import com.pinhuba.common.email.MailTool;
import com.pinhuba.common.module.SessionUser;
import com.pinhuba.common.pack.MailHqlPack;
import com.pinhuba.common.pages.Pager;
import com.pinhuba.common.util.ConstWords;
import com.pinhuba.common.util.EnumUtil;
import com.pinhuba.common.util.UtilTool;
import com.pinhuba.common.util.UtilWork;
import com.pinhuba.common.util.file.properties.SystemConfig;
import com.pinhuba.common.util.security.Base64;
import com.pinhuba.core.dao.IHrmEmployeeDao;
import com.pinhuba.core.dao.IOaMailEmpDao;
import com.pinhuba.core.dao.IOaMailInboxDao;
import com.pinhuba.core.dao.IOaMailSendDao;
import com.pinhuba.core.dao.IOaNetmailInboxDao;
import com.pinhuba.core.dao.IOaNetmailPersonDao;
import com.pinhuba.core.dao.IOaNetmailSendboxDao;
import com.pinhuba.core.dao.IOaNetmailSetDao;
import com.pinhuba.core.dao.IOaNetmailUidDao;
import com.pinhuba.core.iservice.IMailService;
import com.pinhuba.core.pojo.HrmEmployee;
import com.pinhuba.core.pojo.OaMailEmp;
import com.pinhuba.core.pojo.OaMailInbox;
import com.pinhuba.core.pojo.OaMailSend;
import com.pinhuba.core.pojo.OaNetmailInbox;
import com.pinhuba.core.pojo.OaNetmailPerson;
import com.pinhuba.core.pojo.OaNetmailSendbox;
import com.pinhuba.core.pojo.OaNetmailSet;
import com.pinhuba.core.pojo.OaNetmailUid;
@Service
@Transactional
public class MailService implements IMailService {

	private Logger log = Logger.getLogger(this.getClass());
	@Resource
	private IOaMailEmpDao oaMailEmpDao;
	@Resource
	private IOaMailInboxDao oaMailInboxDao;
	@Resource
	private IOaMailSendDao  oaMailSendDao;
	@Resource
	private IHrmEmployeeDao employeeDao;
	@Resource
	private IOaNetmailSetDao oaNetmailSetDao;
	@Resource
	private IOaNetmailPersonDao oaNetmailPersonDao;
	@Resource
	private IOaNetmailSendboxDao oaNetmailSendboxDao;
	@Resource
	private IOaNetmailInboxDao oaNetmailInboxDao;
	@Resource
	private IOaNetmailUidDao oaNetmailUidDao;
	
	public List<OaMailEmp> getAllOaMailInbox(OaMailEmp oaMailEmp, OaMailInbox oaMailInbox, Pager pager) {
		List<OaMailEmp> oaMailEmpList = oaMailEmpDao.findByHqlWherePage(MailHqlPack.getOaMailInboxSql(oaMailEmp,oaMailInbox)+" order by model.oaMailEmpInboxid.oaMailInboxIntime desc", pager);
		
		for (OaMailEmp temp : oaMailEmpList) {
			Hibernate.initialize(temp.getOaMailEmpInboxid());
		}
		
		return oaMailEmpList;
	}
	public int listOaMailInboxCount(OaMailEmp oaMailEmp,OaMailInbox oaMailInbox) {
		int count = oaMailEmpDao.findByHqlWhereCount(MailHqlPack.getOaMailInboxSql(oaMailEmp,oaMailInbox));
		return count;
	}
	public OaMailInbox getOaMailInboxByPk(Integer oaMailEmpInboxid) {
		OaMailInbox oaMailInbox = oaMailInboxDao.getByPK((long)oaMailEmpInboxid);
		return oaMailInbox;
	}
	public void saveOaMailSend(OaMailSend oaMailSend, String isSaveSendBox) {
		
		if(oaMailSend.getOaMailSendType()!= null && oaMailSend.getOaMailSendType() == 2){  //??????????????????
			oaMailSendDao.save(oaMailSend);
		}else{
			//???????????????????????????
			if(isSaveSendBox != null && isSaveSendBox.length()>0){
				oaMailSendDao.save(oaMailSend);  //???????????????
			}

			OaMailInbox oaMailInbox = new OaMailInbox();
			oaMailInbox.setCompanyId(oaMailSend.getCompanyId());
			oaMailInbox.setOaMailInboxAffix(oaMailSend.getOaMailSendAffix());
			oaMailInbox.setOaMailInboxContent(oaMailSend.getOaMailSendContent());
			oaMailInbox.setOaMailInboxIntime(oaMailSend.getOaMailSendTime());
			oaMailInbox.setOaMailInboxIsurgent(oaMailSend.getOaMailSendIsurgent());
			oaMailInbox.setOaMailInboxSendid(oaMailSend.getOaMailSendSenderid());
			oaMailInbox.setOaMailInboxSendName(oaMailSend.getOaMailSendSenderName());
			oaMailInbox.setOaMailInboxSenderdep(oaMailSend.getOaMailSendSenddep());
			oaMailInbox.setOaMailInboxSendtime(oaMailSend.getOaMailSendTime());
			oaMailInbox.setOaMailInboxTitle(oaMailSend.getOaMailSendTitle());
			oaMailInbox.setRecordDate(oaMailSend.getRecordDate());
			oaMailInbox.setRecordId(oaMailSend.getRecordId());
			oaMailInbox.setOaMailInboxEmpNames(oaMailSend.getOaMailSendEmpNames());
			oaMailInbox.setOaMailInboxEmpCSNames(oaMailSend.getOaMailSendEmpCSNames());
			oaMailInbox.setOaMailInboxEmpMSNames(oaMailSend.getOaMailSendEmpMSNames());
			oaMailInbox.setOaMailInboxEmpids(oaMailSend.getOaMailSendEmpids());
			oaMailInbox.setOaMailInboxEmpCSids(oaMailSend.getOaMailSendEmpCSids());
			oaMailInbox.setOaMailInboxReceipt(oaMailSend.getOaMailInboxReceipt());
			
			OaMailInbox Inbox = (OaMailInbox) oaMailInboxDao.save(oaMailInbox); //???????????????
			
			//???????????????
			if(oaMailSend.getOaMailSendEmpids() != null && oaMailSend.getOaMailSendEmpids().length()>0){
			String[] senderids = oaMailSend.getOaMailSendEmpids().substring(0, oaMailSend.getOaMailSendEmpids().length()-1).split(",");
			for (int i = 0; i < senderids.length; i++) {
				OaMailEmp oaMailEmp = new OaMailEmp();
				oaMailEmp.setCompanyId(oaMailSend.getCompanyId());
				oaMailEmp.setOaMailEmpEmpid(senderids[i]);
				oaMailEmp.setOaMailEmpInboxid(Inbox);
				oaMailEmp.setOaMailEmpIsread(2); //??????
				oaMailEmp.setOaMailEmpStatus(1); //??????
				
				oaMailEmpDao.save(oaMailEmp);  //??????????????????????????????
				
			 }
			}	
			//???????????????
			if(oaMailSend.getOaMailSendEmpCSids() != null && oaMailSend.getOaMailSendEmpCSids().length()>0){
			String[] senderCSids = oaMailSend.getOaMailSendEmpCSids().substring(0, oaMailSend.getOaMailSendEmpCSids().length()-1).split(",");
			for (int i = 0; i < senderCSids.length; i++) {
				OaMailEmp oaMailEmp = new OaMailEmp();
				oaMailEmp.setCompanyId(oaMailSend.getCompanyId());
				oaMailEmp.setOaMailEmpEmpid(senderCSids[i]);
				oaMailEmp.setOaMailEmpInboxid(Inbox);
				oaMailEmp.setOaMailEmpIsread(2);
				oaMailEmp.setOaMailEmpStatus(1);
				oaMailEmp.setRecordId(senderCSids[i]);
				oaMailEmp.setRecordDate(oaMailSend.getOaMailSendTime());
				
				oaMailEmpDao.save(oaMailEmp); //??????????????????????????????
				
				
			 }
			}
			//???????????????
			if(oaMailSend.getOaMailSendEmpMSids() != null && oaMailSend.getOaMailSendEmpMSids().length()>0){
			String[] senderMSids = oaMailSend.getOaMailSendEmpMSids().substring(0, oaMailSend.getOaMailSendEmpMSids().length()-1).split(",");
			for (int i = 0; i < senderMSids.length; i++) {
				OaMailEmp oaMailEmp = new OaMailEmp();
				oaMailEmp.setCompanyId(oaMailSend.getCompanyId());
				oaMailEmp.setOaMailEmpEmpid(senderMSids[i]);
				oaMailEmp.setOaMailEmpInboxid(Inbox);
				oaMailEmp.setOaMailEmpIsread(2);
				oaMailEmp.setOaMailEmpStatus(1);
				oaMailEmp.setRecordId(senderMSids[i]);
				oaMailEmp.setRecordDate(oaMailSend.getOaMailSendTime());
				
				oaMailEmpDao.save(oaMailEmp); //??????????????????????????????
				
			 }
			}
			
		}
		
	}
	public List<OaMailSend> getAllOaMailSend(OaMailSend oaMailSend, Pager pager) {
		List<OaMailSend> oaMailSendList = oaMailSendDao.findByHqlWherePage(MailHqlPack.getOaMailSendSql(oaMailSend)+" order by model.oaMailSendTime desc", pager);
		return oaMailSendList;
	}
	public int listOaMailSendCount(OaMailSend oaMailSend) {
		int count = oaMailSendDao.findByHqlWhereCount(MailHqlPack.getOaMailSendSql(oaMailSend));
		return count;
	}
	public OaMailEmp getOaMailEmpByPk(long mailEmpId) {
		OaMailEmp oaMailEmp = oaMailEmpDao.getByPK(mailEmpId);
		Hibernate.initialize(oaMailEmp.getOaMailEmpInboxid());
		
		return oaMailEmp;
	}
	public void saveOaMailEmp(OaMailEmp mailEmp) {
        OaMailInbox mailinbox = oaMailInboxDao.getByPK(mailEmp.getOaMailEmpInboxid().getPrimaryKey());
        if(mailinbox.getOaMailInboxReceipt()!= null && mailinbox.getOaMailInboxReceipt() == 1){ //????????????
        	OaMailInbox newinbox = new OaMailInbox();
        	newinbox.setCompanyId(mailinbox.getCompanyId());
        	newinbox.setOaMailInboxSendid(mailEmp.getOaMailEmpEmpid());
        	HrmEmployee employee = employeeDao.getByPK(mailEmp.getOaMailEmpEmpid());
        	newinbox.setOaMailInboxSendName(employee.getHrmEmployeeName());
        	newinbox.setOaMailInboxSendtime(UtilWork.getNowTime());
        	newinbox.setOaMailInboxIsurgent(1);
        	newinbox.setOaMailInboxTitle(employee.getHrmEmployeeName()+"????????????");
        	newinbox.setOaMailInboxContent("============????????????????????????"+mailinbox.getOaMailInboxTitle()+"============<br/>"+"============????????????"+employee.getHrmEmployeeName()+"  ??????"+UtilWork.getNowTime()+"  ?????????");
        	newinbox.setOaMailInboxIntime(UtilWork.getNowTime());
        	newinbox.setOaMailInboxEmpids(mailinbox.getOaMailInboxSendid()+",");
        	newinbox.setOaMailInboxEmpNames(mailinbox.getOaMailInboxSendName()+",");
        	newinbox.setOaMailInboxReceipt(2);
        	newinbox.setRecordId(mailEmp.getOaMailEmpEmpid());
        	newinbox.setRecordDate(UtilWork.getNowTime());
        	
        	OaMailInbox Inbox = (OaMailInbox) oaMailInboxDao.save(newinbox); //???????????????	
        	
        	OaMailEmp oaMailEmp = new OaMailEmp();
			oaMailEmp.setCompanyId(Inbox.getCompanyId());
			oaMailEmp.setOaMailEmpEmpid(mailinbox.getOaMailInboxSendid());
			oaMailEmp.setOaMailEmpEmpname(mailinbox.getOaMailInboxSendName());
			oaMailEmp.setOaMailEmpInboxid(Inbox);
			oaMailEmp.setOaMailEmpIsread(2);
			oaMailEmp.setOaMailEmpStatus(1);
			oaMailEmp.setRecordId(mailinbox.getOaMailInboxSendid());
			oaMailEmp.setRecordDate(UtilWork.getNowTime());
			
			oaMailEmpDao.save(oaMailEmp); //??????????????????????????????	
        }
        mailEmp.setOaMailEmpIsread(1);
		oaMailEmpDao.save(mailEmp);
		
	}
	public String deleteOaMailSendByPks(long oaMailSendIds) {

			OaMailSend oaMailSend = oaMailSendDao.getByPK(oaMailSendIds);
			oaMailSendDao.remove(oaMailSend);
			
			//????????????
			if(oaMailSend.getOaMailSendAffix() != null && oaMailSend.getOaMailSendAffix().length()>0){
				//????????????????????????????????????????????????
				List<OaMailSend> sendlist = oaMailSendDao.findBySql("select * from oa_mail_send mail where mail.oa_mail_send_affix = '"+oaMailSend.getOaMailSendAffix()+"' and mail.oa_mail_send_id <> "+oaMailSend.getPrimaryKey()+" ", OaMailSend.class);
				//????????????????????????????????????????????????
				List<OaMailInbox> inboxlist = oaMailInboxDao.findBySql("select * from oa_mail_inbox inbox where inbox.oa_mail_inbox_affix = '"+oaMailSend.getOaMailSendAffix()+"' ", OaMailInbox.class);
				
			    if(sendlist.size()>0 || inboxlist.size()>0){ //??????????????????
			    	return null;
			    }else{  //?????????????????????????????????
			    	return oaMailSend.getOaMailSendAffix();
			    }	
		     }
			return null;
		
	}
	public OaMailSend getOaMailSendByPk(long oaMailSendId) {
		OaMailSend oaMailSend = oaMailSendDao.getByPK(oaMailSendId);
		return oaMailSend;
	}
	public void updateOaMailInboxByPks(long[] oaMailInboxIds) {
		for (long l : oaMailInboxIds) {
			OaMailEmp oaMailEmp = oaMailEmpDao.getByPK(l);
			oaMailEmp.setOaMailEmpStatus(2);
			oaMailEmpDao.save(oaMailEmp);
		}
		
	}
	public String deleteOaMailEmpByPks(long oaMailInboxIds) {
	
			OaMailEmp oaMailEmp = oaMailEmpDao.getByPK(oaMailInboxIds);
			oaMailEmpDao.remove(oaMailEmp);
			//???????????????????????????????????????????????????
			List<OaMailEmp> emplist = oaMailEmpDao.findBySql("select * from oa_mail_emp emp where emp.oa_mail_emp_inboxid="+oaMailEmp.getOaMailEmpInboxid().getPrimaryKey()+" and emp.oa_mail_emp_id <>"+oaMailEmp.getPrimaryKey()+"", OaMailEmp.class);
			if(emplist.size() == 0){ //????????????????????????????????????????????????????????????
				OaMailInbox mailinbox = oaMailInboxDao.getByPK(oaMailEmp.getOaMailEmpInboxid().getPrimaryKey());
				oaMailInboxDao.remove(mailinbox);
				
				//??????????????????
				if(mailinbox.getOaMailInboxAffix() != null && mailinbox.getOaMailInboxAffix().length()>0){
					//????????????????????????????????????????????????
					List<OaMailInbox> inboxlist = oaMailInboxDao.findBySql("select * from oa_mail_inbox inbox where inbox.oa_mail_inbox_affix = '"+mailinbox.getOaMailInboxAffix()+"' and inbox.oa_mail_inbox_id <>"+mailinbox.getPrimaryKey()+"", OaMailInbox.class);
					//????????????????????????????????????????????????
					List<OaMailSend> sendlist = oaMailSendDao.findBySql("select * from oa_mail_send mail where mail.oa_mail_send_affix = '"+mailinbox.getOaMailInboxAffix()+"' ", OaMailSend.class);
				    
					 if(sendlist.size()>0 || inboxlist.size()>0){ //??????????????????
					    	return null;
					    }else{  //?????????????????????????????????
					    	return mailinbox.getOaMailInboxAffix();
					    }
				 }
				return null;
			}
			return null;
		
	}
	
	public HrmEmployee getEmployeeByPk(String id) {
		HrmEmployee employee =employeeDao.getByPK(id);
		return employee;
	}
	
	/**
	 * ??????????????????
	 */
	public void setMailRead(long[] ids) {
		for (long l : ids) {
			OaMailEmp mail = oaMailEmpDao.getByPK(l);
			if(mail.getOaMailEmpIsread() == EnumUtil.OA_SMS_INBOX_ISREAD.two.value){
				mail.setOaMailEmpIsread(EnumUtil.OA_SMS_INBOX_ISREAD.one.value);
				oaMailEmpDao.save(mail);
			}
		}
		
	}
	
	public List<OaNetmailSet> getOaNetMailSetList(String empId,int companyId){
		List<OaNetmailSet> list = oaNetmailSetDao.findByHqlWhere(" and model.hrmEmployeeId='"+empId+"' and model.companyId="+companyId);
		for (OaNetmailSet oaNetmailSet : list) {
			oaNetmailSet.setOaNetmailPassword(Base64.getStringFromBase64(oaNetmailSet.getOaNetmailPassword()));
		}
		return list;
	}
	
	public int getOaNetMailSetCount(String empId,int companyId,int type){
		int  row =0;
		if (type==EnumUtil.Net_Mail_Type.Send.value) {
			row = oaNetmailSetDao.findByHqlWhereCount(" and model.hrmEmployeeId='"+empId+"' and model.companyId="+companyId+" and model.oaNetmailIssend="+EnumUtil.SYS_ISACTION.Vaild.value);
		}else if (type==EnumUtil.Net_Mail_Type.Accp.value) {
			row =  oaNetmailSetDao.findByHqlWhereCount(" and model.hrmEmployeeId='"+empId+"' and model.companyId="+companyId+" and model.oaNetmailIsaccp="+EnumUtil.SYS_ISACTION.Vaild.value);
		}
		return row;
	}
	
	public OaNetmailSet getOaNetmailSetByPk(long pk){
		return oaNetmailSetDao.getByPK(pk);
	}
	
	public void saveOaNetMailSet(String empId,int companyId,OaNetmailSet[] sets){
		String time = UtilWork.getNowTime();
		for (OaNetmailSet ms : sets) {
			if (ms.getPrimaryKey()>0) {
				OaNetmailSet tmp = this.getOaNetmailSetByPk(ms.getPrimaryKey());
				ms.setRecordId(tmp.getRecordId());
				ms.setRecordDate(tmp.getRecordDate());
			}else{
				ms.setRecordId(empId);
				ms.setRecordDate(time);
			}
			ms.setOaNetmailPassword(Base64.getBase64FromString(ms.getOaNetmailPassword()));
			ms.setHrmEmployeeId(empId);
			ms.setLastmodiId(empId);
			ms.setLastmodiDate(time);
			ms.setCompanyId(companyId);
			oaNetmailSetDao.save(ms);
		}
	}
	
	public void deleteOaNetMailBySetObj(OaNetmailSet set){
		
	}

	public List<OaNetmailPerson> getOaNetMailPersonListByPager(OaNetmailPerson person,Pager pager){
		List<OaNetmailPerson> list = oaNetmailPersonDao.findByHqlWherePage(MailHqlPack.getOaNetMailPersonSql(person),pager);
		return list;
	}
	
	public int getOaNetMailPersonListByCount(OaNetmailPerson person){
		return oaNetmailPersonDao.findByHqlWhereCount(MailHqlPack.getOaNetMailPersonSql(person));
	}
	
	public OaNetmailPerson saveOaNetMailPerson(OaNetmailPerson person){
		return (OaNetmailPerson) oaNetmailPersonDao.save(person);
	}
	
	public void deleteOaNetMailPersonByPks(long[] pks){
		for (long l : pks) {
			OaNetmailPerson tmp = oaNetmailPersonDao.getByPK(l);
			oaNetmailPersonDao.remove(tmp);
		}
	}
	
	public OaNetmailPerson getOaNetMailPersonByPk(long pk){
		OaNetmailPerson tmp = oaNetmailPersonDao.getByPK(pk);
		return tmp;
	}
	
	public OaNetmailSendbox getOaNetMailSendBoxBypk(long pk){
		return oaNetmailSendboxDao.getByPK(pk);
	}
	
	public void deleteOaNetMailSend(OaNetmailSendbox box){
		oaNetmailSendboxDao.remove(box);
	}
	
	public void deleteOaNetMailInbox(OaNetmailInbox inbox){
		oaNetmailInboxDao.remove(inbox);
	}
	
	
	public void deleteOaNetMailSet(long pk){
		OaNetmailSet set = oaNetmailSetDao.getByPK(pk);
		oaNetmailSetDao.remove(set);
	}
	/**
	 * ????????????
	 * @param box
	 * @return
	 */
	public OaNetmailSendbox saveOaNetMailSendBoxBySketch(OaNetmailSendbox box){
		return (OaNetmailSendbox) oaNetmailSendboxDao.save(box);
	}
	
	public int getOaNetMailSendBoxByCount(OaNetmailSendbox box){
		return oaNetmailSendboxDao.findByHqlWhereCount(MailHqlPack.getOaNetMailSendBoxSql(box));
	}
	
	public List<OaNetmailSendbox> getOaNetMailSendBoxByPager(OaNetmailSendbox box,Pager pager){
		List<OaNetmailSendbox> list = oaNetmailSendboxDao.findByHqlWherePage(MailHqlPack.getOaNetMailSendBoxSql(box)+" order by model.oaNetmailSendTime desc",pager);
		for (OaNetmailSendbox oaNetmailSendbox : list) {
			if (oaNetmailSendbox.getOaNetmailSendAdders()==null||oaNetmailSendbox.getOaNetmailSendAdders().trim().length()==0) {
				oaNetmailSendbox.setOaNetmailSendAdders("<b>?????????????????????</b>");
			}
			if (oaNetmailSendbox.getOaNetmailSendEmpids()==null||oaNetmailSendbox.getOaNetmailSendEmpids().trim().length()==0) {
				oaNetmailSendbox.setOaNetmailSendEmpNames("<b>????????????????????????</b>");
			}else{
				oaNetmailSendbox.setOaNetmailSendEmpNames(this.getEmployeeNamesByIds(oaNetmailSendbox.getOaNetmailSendEmpids()));
			}
		}
		return list;
	}
	
	private String getEmployeeNamesByIds(String ids){
		StringBuffer result = new StringBuffer();
		if (ids==null||ids.length()==0) {
			return result.toString();
		}
		if (ids.charAt(ids.length() - 1) == ',') {
			ids = ids.substring(0, ids.length() - 1);
		}
		if (ids.length() > 0) {
			List<Object[]> list = employeeDao.findBySqlObjList("select hrm_employee_id,hrm_employee_name from hrm_employee where hrm_employee_id in (' + " + ids +" + ' )");
			for (Object[] obj : list) {
				result.append(obj[1].toString()+",");
			}
		}
		return result.toString();
	}
	
	
	public boolean saveNetmailSend(OaNetmailSendbox box,boolean issavesend,OaNetmailSet mailSet,SessionUser user,String files) throws Exception{
		//?????????????????????
		if (issavesend) {
			 oaNetmailSendboxDao.save(box);
		}else{//?????????????????????
			 if (box.getPrimaryKey()>0) {
				//??????????????????
				OaNetmailSendbox tm = oaNetmailSendboxDao.getByPK(box.getPrimaryKey());
				oaNetmailSendboxDao.remove(tm);
			 }
		}
		
		String sendempIds = box.getOaNetmailSendEmpids();
		//??????????????????
		if (sendempIds!=null&&sendempIds.length()>0) {

			OaMailInbox oaMailInbox = new OaMailInbox();
			oaMailInbox.setCompanyId(box.getCompanyId());
			oaMailInbox.setOaMailInboxAffix(box.getOaNetmailSendAffix());
			oaMailInbox.setOaMailInboxContent(box.getOaNetmailSendContent());
			oaMailInbox.setOaMailInboxIntime(box.getOaNetmailSendTime());
			oaMailInbox.setOaMailInboxIsurgent(box.getOaNetmailSendIsurgent());
			oaMailInbox.setOaMailInboxSendid(box.getOaNetmailSendEmpid());
			oaMailInbox.setOaMailInboxSendName(user.getEmployeeName());
			oaMailInbox.setOaMailInboxSenderdep(user.getDepartmentInfo().getHrmDepName());
			oaMailInbox.setOaMailInboxSendtime(box.getOaNetmailSendTime());
			oaMailInbox.setOaMailInboxTitle(box.getOaNetmailSendTitle());
			oaMailInbox.setRecordDate(box.getRecordDate());
			oaMailInbox.setRecordId(box.getRecordId());
			oaMailInbox.setOaMailInboxEmpNames(box.getOaNetmailSendEmpNames());
			oaMailInbox.setOaMailInboxEmpCSNames("");
			oaMailInbox.setOaMailInboxEmpMSNames("");
			oaMailInbox.setOaMailInboxEmpids(sendempIds);
			oaMailInbox.setOaMailInboxEmpCSids("");
			oaMailInbox.setOaMailInboxReceipt(box.getOaNetmailReceipt());

			OaMailInbox Inbox = (OaMailInbox) oaMailInboxDao.save(oaMailInbox); // ???????????????

			// ???????????????

			String[] senderids = sendempIds.split(",");
			for (int i = 0; i < senderids.length; i++) {
				if (senderids[i] != null && senderids[i].length() > 0) {
					OaMailEmp oaMailEmp = new OaMailEmp();
					oaMailEmp.setCompanyId((int) user.getCompanyId());
					oaMailEmp.setOaMailEmpEmpid(senderids[i]);
					oaMailEmp.setOaMailEmpInboxid(Inbox);
					oaMailEmp.setOaMailEmpIsread(EnumUtil.OA_SMS_INBOX_ISREAD.two.value); // ??????
					oaMailEmp.setOaMailEmpStatus(EnumUtil.OA_MAIL_STATUS.APPECT.value); // ??????

					oaMailEmpDao.save(oaMailEmp); // ??????????????????????????????
				}
			}
		}
		
		//??????????????????
		MailTool mailtool = new MailTool();
		mailtool.setMailTitle(box.getOaNetmailSendTitle());
		mailtool.setMailRec(box.getOaNetmailReceipt());
		mailtool.setMailContent(box.getOaNetmailSendContent());
		int urgent = box.getOaNetmailSendIsurgent();
		if(urgent==EnumUtil.OA_CALENDER_LEVEL.one.value||urgent==EnumUtil.OA_CALENDER_LEVEL.two.value){
			mailtool.setMailPrj(1);
		}else if (urgent==EnumUtil.OA_CALENDER_LEVEL.three.value) {
			mailtool.setMailPrj(3);
		}else{
			mailtool.setMailPrj(5);
		}
		mailtool.setOaNetmailSet(mailSet);
		
		//?????????
		String[] adders = box.getOaNetmailSendAdders().split(";");
		for (String mail : adders) {
			if (mail != null && mail.length() > 0 && Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", mail)) {// ?????????e-mail??????
				mailtool.getToAdderSet().add(mail);
			}
		}
		if (files!=null&&files.length()>0) {
			//????????????
			String fs[] = files.split(",");
			for (String str : fs) {
				if(str!=null&&str.length()>0){
					String[] tmps = str.split("\\|");
					mailtool.getFiles().put(tmps[0], Base64.getStringFromBase64(tmps[1]));
				}
			}
		}
		boolean bl = mailtool.sendMail();
		
		return bl;
	}
	
	/**
	 * ????????????
	 * @param context
	 * @param request
	 * @param setIds
	 * @throws Exception
	 */
	public int saveNetMailInboxGetServer(ServletContext context,HttpServletRequest request,long[] setIds) throws Exception{
		
		int count=0;
		String empId = UtilTool.getEmployeeId(request);
		try {
			String path = SystemConfig.getParam("erp.mail.filePath");
			path += UtilTool.getCompanyAndUserPath(request, false)+ConstWords.septor;
			int companyId = UtilTool.getCompanyId(request);
			log.info("??????????????????..."+UtilWork.getNowTime());
			ConstWords.mailIsAccpMap.put(empId, true);
			
			for (long l : setIds) {
				OaNetmailSet set = oaNetmailSetDao.getByPK(l);
				MailTool mt = new MailTool();
				mt.setOaNetmailSet(set);
				mt.setRecFilePath(path);
				
				//?????????????????????
				Set<String> uidset = this.getOaNetMailUidBySetId(l, empId, companyId);
				//???????????????
				ArrayList<MailReceiveBean> mrlist = mt.receiveMail(uidset);
				count +=  mrlist.size();
				//???????????????
				this.saveNetMailList(context, request, empId, companyId, set, mrlist);
			}
		} catch (Exception e) {
			ConstWords.mailIsAccpMap.put(empId, false);
			throw new Exception("?????????????????????????????????");
		}
		log.info("??????????????????..."+UtilWork.getNowTime());
		ConstWords.mailIsAccpMap.put(empId, false);
		return count;
	}
	
	/**
	 * ?????????????????????
	 * @param sid
	 * @param empId
	 * @param companyId
	 * @return
	 */
	private Set<String> getOaNetMailUidBySetId(long sid,String empId,int companyId){
		OaNetmailSet set = oaNetmailSetDao.getByPK(sid);
		
		Set<String> uidset = new HashSet<String>();
		List<OaNetmailUid> list = oaNetmailUidDao.findByHqlWhere(" and model.oaMailSetId like '%"+set.getOaNetmailFrom().trim()+"%' and model.oaMailEmpId='"+empId+"' and model.companyId="+companyId);
		for (OaNetmailUid oaNetmailUid : list) {
			uidset.add(oaNetmailUid.getOaMailUid());
		}
		return uidset;
	}
	
	/**
	 * ???????????????
	 * @param empId
	 * @param companyId
	 * @param setobj
	 * @param mrlist
	 */
	private void saveNetMailList(ServletContext context,HttpServletRequest request,String empId,int companyId,OaNetmailSet setobj,ArrayList<MailReceiveBean> mrlist){
		for (MailReceiveBean mailReceiveBean : mrlist) {
			//??????uid?????????
			OaNetmailUid mailUid = new OaNetmailUid();
			mailUid.setOaMailUid(mailReceiveBean.getMailUid());
			mailUid.setOaMailEmpId(empId);
			mailUid.setCompanyId(companyId);
			mailUid.setOaMailSetId(setobj.getOaNetmailFrom().trim());
			mailUid.setOaMailTime(UtilWork.getNowTime());
			
			oaNetmailUidDao.save(mailUid);
			
			OaNetmailInbox inbox = new OaNetmailInbox();
			
			inbox.setOaNetmailInboxSender(mailReceiveBean.getMailFromPerson());
			inbox.setOaNetmailInboxTime(mailReceiveBean.getSentDate());
			inbox.setOaNetmailInboxTitle(mailReceiveBean.getMailSubject());
			inbox.setOaNetmailInboxContent(mailReceiveBean.getBodyText());
			inbox.setOaNetmailSetFrom(mailReceiveBean.getMailFrom());
			if (mailReceiveBean.getAttachMent()!=null&&mailReceiveBean.getAttachMent().length()>0) {
				String ids = UtilTool.saveAttachments(context, request, mailReceiveBean.getAttachMent());
				inbox.setOaNetmailInboxAffix(ids);
			}
			inbox.setOaNetmailInboxEmpid(empId);
			inbox.setRecordId(empId);
			inbox.setRecordDate(UtilWork.getNowTime());
			inbox.setOaNetmailIsRead(mailReceiveBean.getIsRead());
			inbox.setOaNetmailUrgent(mailReceiveBean.getUrgent());
			inbox.setOaNetmailMessageId(mailReceiveBean.getMessageId());
			inbox.setOaNetmailSetId(setobj.getOaNetmailFrom().trim());
			inbox.setOaNetmailReplySign(mailReceiveBean.getReplySign());
			inbox.setCompanyId(companyId);
			oaNetmailInboxDao.save(inbox);
		}
	}
	
	public int getNetMailInboxCount(OaNetmailInbox inbox){
		return oaNetmailInboxDao.findBySqlCount(MailHqlPack.getOaNetMailInboxSql(inbox));
	}
	
	public List<Object[]> getNetmailInboxByPager(OaNetmailInbox inbox,Pager pager){
		List<Object[]> objlist = oaNetmailInboxDao.findBySqlObjListByPager(MailHqlPack.getOaNetMailInboxSql(inbox)+" order by oa_netmail_inbox_time desc", pager);
		return objlist;
	}
	
	public List<OaNetmailInbox> getNetmailInboxBySetId(OaNetmailInbox inbox){
		List<OaNetmailInbox> list = oaNetmailInboxDao.findByHqlWhere(" and model.oaNetmailSetId like '%"+inbox.getOaNetmailSetId()+"%' and model.oaNetmailInboxEmpid='"+inbox.getOaNetmailInboxEmpid()+"' and model.companyId="+inbox.getCompanyId());
		return list;
	}
	
	public List<OaNetmailSendbox> getNetmailSendboxBySetId(OaNetmailSendbox sendbox){
		List<OaNetmailSendbox> list = oaNetmailSendboxDao.findByHqlWhere(MailHqlPack.getOaNetMailSendBoxSql(sendbox));
		return list;
	}
	
	public void deleteOanetmailUid(OaNetmailUid uid){
		List<OaNetmailUid> list = oaNetmailUidDao.findByHqlWhere(" and model.oaMailSetId like '%"+uid.getOaMailSetId()+"%' and model.oaMailEmpId='"+uid.getOaMailEmpId()+"' and model.companyId="+uid.getCompanyId());
		for (OaNetmailUid oaNetmailUid : list) {
			oaNetmailUidDao.remove(oaNetmailUid);
		}
	}
	
	public void updateNetMailReadStatus(long[] pks){
		String ids = "";
		for (int i = 0; i < pks.length; i++) {
			if(i==pks.length-1){
				ids+=pks[i];
			}else{
				ids+=pks[i]+",";
			}
		}
		if(ids.length()>0){
			oaNetmailInboxDao.executeSql("update oa_netmail_inbox set oa_netmail_isread = "+EnumUtil.OA_SMS_INBOX_ISREAD.one.value+" where oa_netmail_inbox_id in ( "+ids+" )");
		}
	}
	
	public void updateNetMailRepSign(long pk){
		oaNetmailInboxDao.executeSql("update oa_netmail_inbox set oa_netmail_replysign = "+EnumUtil.OA_MAIL_RECEIPT.TWO.value+" where oa_netmail_inbox_id="+pk);
	}
	
	public OaNetmailInbox getOaNetmailInboxById(long pk){
		return oaNetmailInboxDao.getByPK(pk);
	}
	
	/**
	 * ??????????????????
	 * @param set
	 * @return
	 */
	public String testNetMail(OaNetmailSet set){
		MailTool mt = new MailTool(set);
		String msg="";
		//????????????
		boolean sendbl=false;
		//????????????
		boolean recbl=false;
		if(set.getOaNetmailIssend()==EnumUtil.SYS_ISACTION.Vaild.value){
			try {
				sendbl = mt.mailSendTest();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			if (sendbl) {
				msg += "<font color='green'>?????????????????????</font>";
			}else{
				msg += "<font color='red'>?????????????????????</font>";
			}
		}
		
		if (set.getOaNetmailIsaccp()==EnumUtil.SYS_ISACTION.Vaild.value) {
			try {
				recbl = mt.mailReceiveTest();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			
			
			if (recbl) {
				msg += "<font color='green'>?????????????????????</font>";
			}else{
				msg += "<font color='red'>?????????????????????</font>";
			}
		}
		
		if (msg.length()==0) {
			msg="??????????????????????????????????????????";
		}
		return msg;
	}
}
