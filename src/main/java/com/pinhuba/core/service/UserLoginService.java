package com.pinhuba.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinhuba.common.module.SessionUser;
import com.pinhuba.common.pack.SystemPack;
import com.pinhuba.common.util.EnumUtil;
import com.pinhuba.common.util.LoginContext;
import com.pinhuba.common.util.UtilWork;
import com.pinhuba.common.util.security.Base64;
import com.pinhuba.core.dao.IHrmDepartmentDao;
import com.pinhuba.core.dao.IHrmEmployeeDao;
import com.pinhuba.core.dao.IHrmPostDao;
import com.pinhuba.core.dao.ISysCompanyInfoDao;
import com.pinhuba.core.dao.ISysCompanyMethodsDao;
import com.pinhuba.core.dao.ISysConfigDao;
import com.pinhuba.core.dao.ISysMethodBtnDao;
import com.pinhuba.core.dao.ISysMethodInfoDao;
import com.pinhuba.core.dao.ISysParamDao;
import com.pinhuba.core.dao.ISysRoleBindDao;
import com.pinhuba.core.dao.ISysRoleBtnDao;
import com.pinhuba.core.dao.ISysRoleDetailDao;
import com.pinhuba.core.dao.ISysUserBtnsDao;
import com.pinhuba.core.dao.ISysUserGroupDetailDao;
import com.pinhuba.core.dao.ISysUserInfoDao;
import com.pinhuba.core.dao.ISysUserMethodsDao;
import com.pinhuba.core.iservice.IUserLoginService;
import com.pinhuba.core.pojo.HrmDepartment;
import com.pinhuba.core.pojo.HrmEmployee;
import com.pinhuba.core.pojo.HrmPost;
import com.pinhuba.core.pojo.SysCompanyInfo;
import com.pinhuba.core.pojo.SysCompanyMethods;
import com.pinhuba.core.pojo.SysConfig;
import com.pinhuba.core.pojo.SysMethodBtn;
import com.pinhuba.core.pojo.SysMethodInfo;
import com.pinhuba.core.pojo.SysParam;
import com.pinhuba.core.pojo.SysRoleBind;
import com.pinhuba.core.pojo.SysRoleBtn;
import com.pinhuba.core.pojo.SysRoleDetail;
import com.pinhuba.core.pojo.SysUserBtns;
import com.pinhuba.core.pojo.SysUserGroupDetail;
import com.pinhuba.core.pojo.SysUserInfo;
import com.pinhuba.core.pojo.SysUserMethods;
@Service
@Transactional
public class UserLoginService implements IUserLoginService {
	@Resource
	private ISysUserInfoDao sysUserInfoDao;
	@Resource
	private IHrmEmployeeDao hrmEmployeeDao;
	@Resource
	private IHrmDepartmentDao hrmDepartmentDao;
	@Resource
	private ISysCompanyInfoDao sysCompanyInfoDao;
	@Resource
	private ISysCompanyMethodsDao sysCompanyMethodsDao;
	@Resource
	private ISysMethodInfoDao sysMethodInfoDao;
	@Resource
	private ISysMethodBtnDao sysMethodBtnDao;
	@Resource
	private ISysUserMethodsDao sysUserMethodsDao;
	@Resource
	private ISysUserBtnsDao sysUserBtnsDao;
	@Resource
	private ISysRoleBindDao rolebindDao;
	@Resource
	private ISysRoleDetailDao roledetailDao;
	@Resource
	private ISysRoleBtnDao roleBtnDao;
	@Resource
	private ISysUserGroupDetailDao usergroupdetailDao;
	@Resource
	private IHrmPostDao hrmPostDao;
	@Resource
	private ISysConfigDao sysConfigDao;
	@Resource
	private ISysParamDao sysParamDao;

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	public List<SysMethodInfo> getAllMethodInfoByLevel() {
		List<SysMethodInfo> sysMethodInfoList = sysMethodInfoDao.findByHqlWhere(" and model.methodLevel=-1 and model.methodNo<>-1 order by model.methodNo");
		return sysMethodInfoList;
	}
	
	
	/**
	 * ???????????????????????????
	 * @return
	 */
	public Set<Integer> getAllMethodBtn() {
		List<SysMethodBtn> btnList = sysMethodBtnDao.list();
		Set<Integer> btns =new HashSet<Integer>();
		for (SysMethodBtn sysMethodBtn : btnList) {
			btns.add((int) sysMethodBtn.getPrimaryKey());
		}
		return btns;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param pk
	 * @return
	 */
	public SysMethodInfo getMethodInfoByPk(String pk) {
		SysMethodInfo sysmethodInfo = sysMethodInfoDao.getByPK(pk);
		return sysmethodInfo;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param id
	 * @return
	 */
	public SysCompanyInfo getCompanyInfoByPk(long id) {
		SysCompanyInfo companyInfo = sysCompanyInfoDao.getByPK(id);
		return companyInfo;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param cid
	 * @return
	 */
	public List<SysUserInfo> getUserByCompanyPk(int cid) {
		List<SysUserInfo> sysUserList = sysUserInfoDao.findByProperty("companyId", cid);
		return sysUserList;
	}

	/**
	 * ????????????????????????????????????????????????
	 * 
	 * @param userName
	 * @param cid
	 * @return
	 */
	public SysUserInfo getUserInfoByCompanyIdAndUserName(String userName, int cid) {
		SysUserInfo userTemp = null;
		List<SysUserInfo> sysUserList = sysUserInfoDao.findByHqlWhere(" and model.companyId=" + cid + " and model.userName ='" + userName + "'");
		if (sysUserList.size() == 1) {
			userTemp = sysUserList.get(0);
		}
		return userTemp;
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param companyCode
	 * @return
	 */
	public SysCompanyInfo getCompanyInfoByCode(String companyCode) {
		SysCompanyInfo companyInfo = null;
		List<SysCompanyInfo> companyInfoList = sysCompanyInfoDao.findByProperty("companyInfoCode", companyCode);
		if (companyInfoList.size() == 1) {
			companyInfo = companyInfoList.get(0);
		}
		return companyInfo;
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????
	 * 
	 * @param companyCode
	 * @return
	 */
	public SysCompanyInfo vaildityCompany(String companyCode) {
		SysCompanyInfo temp = null;
		String nowDate = UtilWork.getToday();
		if (companyCode != null && companyCode.trim().length() > 0) {
			SysCompanyInfo companyInfo = this.getCompanyInfoByCode(companyCode);
			if (companyInfo != null) {
				if (companyInfo.getCompanyInfoType() == EnumUtil.SYS_COMPANY_TYPE.SYSTEM.value) {//??????????????????????????????
					return companyInfo;
				}
				boolean sbl = UtilWork.checkDay(nowDate, companyInfo.getCompanyInfoSdate()) || nowDate.equals(companyInfo.getCompanyInfoSdate());
				boolean ebl = UtilWork.checkDay(companyInfo.getCompanyInfoEdate(), nowDate) || nowDate.equals(companyInfo.getCompanyInfoEdate());
				if (sbl && ebl) {
					temp = companyInfo;
				}
			}
		}
		return temp;
	}

	/**
	 * ??????????????????
	 * 
	 * @param companyCode
	 * @param userName
	 * @param userPwd
	 * @return
	 */
	public SysUserInfo vaildityUserInfo(SysCompanyInfo companyInfo, String userName, String userPwd) {
		SysUserInfo temp = null;
		SysUserInfo userInfoTmp = this.getUserInfoByCompanyIdAndUserName(userName, (int) companyInfo.getPrimaryKey());
		if (userInfoTmp != null && userInfoTmp.getUserAction() == EnumUtil.SYS_ISACTION.Vaild.value) {
			// ??????????????????
			String parsePwd = Base64.getBase64FromString(userPwd);
			if (userPwd != null && userPwd.trim().length() > 0 && parsePwd.equals(userInfoTmp.getUserpassword())) {
				temp = userInfoTmp;
			}
		}
		return temp;
	}
	
	public SysUserInfo vaildityUserInfo(SysCompanyInfo companyInfo, String userName) {
		return this.getUserInfoByCompanyIdAndUserName(userName, (int) companyInfo.getPrimaryKey());
	}

	/**
	 * ????????????????????????
	 * 
	 * @param cpk
	 * @return
	 */
	public List<SysMethodInfo> getCompanyMethodsByCPk(int cpk) {
		List<SysMethodInfo> sysmethodlist = this.getAllMethodInfoByLevel();
		List<SysMethodInfo> list = new ArrayList<SysMethodInfo>();
		SysCompanyInfo companyInfo = sysCompanyInfoDao.getByPK((long)cpk);
		if (companyInfo.getCompanyInfoType() == EnumUtil.SYS_COMPANY_TYPE.SYSTEM.value) {
			for (SysMethodInfo sm : sysmethodlist) {
				if (sm.getIsAction() == EnumUtil.SYS_ISACTION.Vaild.value) {
					list.add(sm);
				}
			}
			return list;
		}
		List<SysCompanyMethods> companyMethodsList = sysCompanyMethodsDao.findByProperty("companyId", cpk);
		
		for (SysCompanyMethods sysCompanyMethod : companyMethodsList) {
			for (SysMethodInfo sysMethodInfo : sysmethodlist) {
				if (sysMethodInfo.getPrimaryKey().equals(sysCompanyMethod.getMethodInfoId())&&sysMethodInfo.getIsAction() == EnumUtil.SYS_ISACTION.Vaild.value) {
					list.add(sysMethodInfo);
				}
			}
		}
		return list;
	}
	
	public Map<String, String> getSysParamToMap(long companyId){
		List<SysParam> list = sysParamDao.findByHqlWhere(" and model.companyId = "+companyId);
		Map<String, String> parMap = new HashMap<String, String>();
		for (SysParam sysParam : list) {
			parMap.put(sysParam.getParamIndex(), sysParam.getParamValue());
		}
		return parMap;
	}
	
	/**
	 * ????????????????????????
	 * 
	 * @param companyCode
	 * @param userName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SessionUser packageUserInfo(String companyCode, String userName, String projectCode) {
		SessionUser suser = new SessionUser();
		// 1.????????????
		SysCompanyInfo companyInfo = this.getCompanyInfoByCode(companyCode);
		suser.setCompanyId(companyInfo.getPrimaryKey());
		suser.setCompanyCode(companyCode);
		suser.setCompanyInfoWareHouseCount(companyInfo.getCompanyInfoWarehousecount());
		suser.setCompanyInfoUserCount(companyInfo.getCompanyInfoUsercount());
		suser.setCompanyInfo(companyInfo);
		suser.setCompanyName(companyInfo.getCompanyInfoName());
		suser.setCompanyShortName(companyInfo.getCompanyInfoShortname());
		
		//1.2??????????????????????????????map
		Map<String, String> paramMap = this.getSysParamToMap(companyInfo.getPrimaryKey());
		suser.setParamMap(paramMap);
		//??????????????????
		List<SysMethodInfo> sysCompanyMethodList = this.getCompanyMethodsByCPk(Integer.parseInt(companyInfo.getPrimaryKey() + ""));
		suser.setCompanyMethodsList(sysCompanyMethodList);
		// 2.????????????
		SysUserInfo userInfoTmp = this.getUserInfoByCompanyIdAndUserName(userName, (int) companyInfo.getPrimaryKey());
		suser.setUserName(userName);
		suser.setUserInfo(userInfoTmp);
		
		if (companyInfo.getCompanyInfoType() == EnumUtil.SYS_COMPANY_TYPE.SYSTEM.value) {
			// ????????????
			HrmEmployee employee = new HrmEmployee();
			employee.setPrimaryKey(String.valueOf(userInfoTmp.getPrimaryKey()));
			// ??????????????????
			HrmDepartment dept = new HrmDepartment();
			dept.setPrimaryKey(-1);
			// ????????????
			Set<String> userMethods =new HashSet<String>();
			List<SysMethodInfo> tmplist = this.getAllMethodInfoByLevel();
			for (SysMethodInfo sysMethodInfo : tmplist) {
				userMethods.add(sysMethodInfo.getPrimaryKey());
			}
			
			if(userInfoTmp.getUserType() == EnumUtil.SYS_USER_TYPE.TSET.value) {
				employee.setHrmEmployeeName("????????????");
				dept.setHrmDepName("?????????");
				suser.setEmployeeName("????????????");
				suser.setEmployeeDeptName("?????????");
			}else if(userInfoTmp.getUserType() == EnumUtil.SYS_USER_TYPE.SYSTEM.value){
				employee.setHrmEmployeeName("????????????");
				dept.setHrmDepName("?????????");
				suser.setEmployeeName("????????????");
				suser.setEmployeeDeptName("?????????");
			}
			suser.setEmployeeInfo(employee);
			suser.setDepartmentInfo(dept);
			suser.setUserMethodsSet(userMethods);
			suser.setUserBtnsSet(this.getAllMethodBtn());
		}else{
			//2010-1-15?????????????????????????????????
			if(userInfoTmp.getUserType() == EnumUtil.SYS_USER_TYPE.SYSTEM.value){
				//??????????????????????????????
				// ????????????
				HrmEmployee employee = new HrmEmployee();
				employee.setPrimaryKey(String.valueOf(userInfoTmp.getPrimaryKey()));
				employee.setHrmEmployeeName("???????????????");
				
				// ??????????????????
				HrmDepartment dept = new HrmDepartment();
				dept.setPrimaryKey(-1);
				dept.setHrmDepName("?????????");
				// ????????????
				Set<String> userMethods =new HashSet<String>();
				for (SysMethodInfo sysMethodInfo : sysCompanyMethodList) {
					userMethods.add(sysMethodInfo.getPrimaryKey());
				}
				suser.setEmployeeName("???????????????");
				suser.setEmployeeDeptName("?????????");
				suser.setEmployeeInfo(employee);
				suser.setDepartmentInfo(dept);
				suser.setUserMethodsSet(userMethods);
				suser.setUserBtnsSet(this.getAllMethodBtn());
				
				//????????????????????????????????? ?????????????????? 2014-04-04 JC
				//???????????????????????????
				List<SysMethodInfo> userModuleMethods = new ArrayList<SysMethodInfo>();
				
				for(SysMethodInfo sysMethod : suser.getCompanyMethodsList()){
					boolean bl =false;
					if(suser.getUserMethodsSet()!=null && suser.getUserMethodsSet().size()>0){
						Iterator<String> it = suser.getUserMethodsSet().iterator();
						while(it.hasNext()){
							String id = it.next();
							if(sysMethod.getPrimaryKey().equals(id)){
								bl = true;
								break;
							}
						}
					}
					if(bl){
						userModuleMethods.add(sysMethod);
					}
				}
				
				//?????????????????????????????????
				CaseInsensitiveComparator comp =new CaseInsensitiveComparator();
				Collections.sort(userModuleMethods,comp);
				suser.setUserModuleMethods(userModuleMethods);
				
			}else{
				// 3.????????????
				HrmEmployee employee = hrmEmployeeDao.getByPK(userInfoTmp.getHrmEmployeeId());
				suser.setEmployeeName(employee.getHrmEmployeeName());
				suser.setEmployeeInfo(employee);
	
				// 4.??????????????????
				HrmDepartment dept = hrmDepartmentDao.getByPK(employee.getHrmEmployeeDepid().longValue());
				suser.setEmployeeDeptName(dept.getHrmDepName());
				suser.setDepartmentInfo(dept);
	
				// 5.???????????????
				if (employee.getHrmEmployeePostId() != null && employee.getHrmEmployeePostId().intValue() > 0) {
					HrmPost mainPost = hrmPostDao.getByPK((long) employee.getHrmEmployeePostId());
					suser.setMainPost(mainPost);
				}
				// 6.??????????????????
				if (employee.getHrmPartPost() != null && employee.getHrmPartPost().length() > 0) {
					String[] pids = employee.getHrmPartPost().split(",");
					suser.setPartPosts(this.getPartPostsByPostIds(pids));
				}
				//6.1??????????????????
				Set<Integer> roleSet = this.getRoleIdsByUser(suser);
				suser.setRoleIds(roleSet);
				//6.2????????????
				SysUserMethods userMethodsList =  this.getSysUserMethodsByUid(userInfoTmp.getPrimaryKey());
				suser.setSysUserMethodsList(userMethodsList);
				
				SysUserBtns userBtns =  this.getSysUserBtnsByUid(userInfoTmp.getPrimaryKey());
				suser.setSysUserBtns(userBtns);
				
				// 7.??????????????????
				suser.setUserMethodsSet(this.getUserCompanyMethods(suser));
				suser.setUserBtnsSet(this.getUserMethodBtn(suser));
				
				// 8.???????????????????????????
				List<SysMethodInfo> userModuleMethods = new ArrayList<SysMethodInfo>();
				
				for(SysMethodInfo sysMethod : suser.getCompanyMethodsList()){
					boolean bl =false;
					if(suser.getUserMethodsSet()!=null && suser.getUserMethodsSet().size()>0){
						Iterator<String> it = suser.getUserMethodsSet().iterator();
						while(it.hasNext()){
							String id = it.next();
							if(sysMethod.getPrimaryKey().equals(id)){
								bl = true;
								break;
							}
						}
					}
					if(bl){
						userModuleMethods.add(sysMethod);
					}
				}
				
				//?????????????????????????????????
				CaseInsensitiveComparator comp =new CaseInsensitiveComparator();
				Collections.sort(userModuleMethods,comp);
				suser.setUserModuleMethods(userModuleMethods);
			}
		}
		//8.??????????????????
		SysConfig sysconfig = getSysconfigByCode(projectCode);
		suser.setSysconfig(sysconfig);
		return suser;
	}

	public SysConfig getSysconfigByCode(String code){
		List<SysConfig> list = sysConfigDao.findByHqlWhere(" and methodId ='"+code+"'");
		SysConfig sysconfig = null;
		if (list.size()>0) {
			sysconfig = list.get(0);
		}
		return sysconfig;
	}
	
	
	/**
	 * ????????????id????????????????????????
	 */
	public List<HrmPost> getPartPostsByPostIds(String[] postIds) {
		List<HrmPost> postsList = new ArrayList<HrmPost>();
		for (String pid : postIds) {
			if (pid.length()>0) {
				postsList.add(hrmPostDao.getByPK(Long.parseLong(pid)));
			}
		}
		return postsList;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param userId
	 * @return
	 */
	public Set<String> getUserCompanyMethods(SessionUser user) {
		// ??????????????????
		Set<String> methodIds = this.getMethodIdsByRoleIds(user.getRoleIds(), user.getSysUserMethodsList());
		return methodIds;
	}
	
	public Set<Integer> getUserMethodBtn(SessionUser user) {
		Set<Integer> btnIds = this.getBtnIdsByRoleIds(user.getRoleIds(), user.getSysUserBtns());
		return btnIds;
	}
	
	public SysCompanyInfo getCompanyInfoByUserId(long userId){
		SysUserInfo user = sysUserInfoDao.getByPK(userId);
		SysCompanyInfo company =null;
		if (user!=null) {
			company = sysCompanyInfoDao.getByPK((long)user.getCompanyId());
		}
		return company;
	}
	
	
	/**
	 * ????????????????????????(?????????????????????)
	 * 
	 * @param userId
	 * @return
	 */
	public Set<String> getUserCompanyMethods(long userId,int companyType) {
		Set<String> ids = new HashSet<String>(); 
		if (companyType== EnumUtil.SYS_COMPANY_TYPE.SYSTEM.value) {
			List<SysMethodInfo> sysMethodInfoList =this.getAllMethodInfoByLevel();
			for (SysMethodInfo sysMethodInfo : sysMethodInfoList) {
				ids.add(sysMethodInfo.getPrimaryKey());
			}
		}else{
			
			SessionUser suser = new SessionUser();
			// 2.????????????
			SysUserInfo userInfoTmp = sysUserInfoDao.getByPK(userId);
			suser.setUserInfo(userInfoTmp);
			//??????????????????
			List<SysMethodInfo> sysCompanyMethodList = this.getCompanyMethodsByCPk(userInfoTmp.getCompanyId());
			suser.setCompanyMethodsList(sysCompanyMethodList);
			//2010-1-15?????????????????????????????????
			if(userInfoTmp.getUserType() == EnumUtil.SYS_USER_TYPE.SYSTEM.value){
				//??????????????????????????????
				// ????????????
				HrmEmployee employee = new HrmEmployee();
				employee.setPrimaryKey(String.valueOf(userInfoTmp.getPrimaryKey()));
				employee.setHrmEmployeeName("?????????????????????");
				
				// ??????????????????
				HrmDepartment dept = new HrmDepartment();
				dept.setPrimaryKey(-1);
				dept.setHrmDepName("????????????");
				// ????????????
				Set<String> userMethods =new HashSet<String>();
				for (SysMethodInfo sysMethodInfo : sysCompanyMethodList) {
					userMethods.add(sysMethodInfo.getPrimaryKey());
				}
				suser.setEmployeeName("?????????????????????");
				suser.setEmployeeDeptName("????????????");
				suser.setEmployeeInfo(employee);
				suser.setDepartmentInfo(dept);
				suser.setUserMethodsSet(userMethods);
			}else{
				// 3.????????????
				HrmEmployee employee = hrmEmployeeDao.getByPK(userInfoTmp.getHrmEmployeeId());
				suser.setEmployeeInfo(employee);
		
				// 4.??????????????????
				HrmDepartment dept = hrmDepartmentDao.getByPK(employee.getHrmEmployeeDepid().longValue());
				suser.setDepartmentInfo(dept);
		
				// 5.???????????????
				if (employee.getHrmEmployeePostId() != null && employee.getHrmEmployeePostId().intValue() > 0) {
					HrmPost mainPost = hrmPostDao.getByPK((long) employee.getHrmEmployeePostId());
					suser.setMainPost(mainPost);
				}
				// 6.??????????????????
				if (employee.getHrmPartPost() != null && employee.getHrmPartPost().length() > 0) {
					String[] pids = employee.getHrmPartPost().split(",");
					suser.setPartPosts(this.getPartPostsByPostIds(pids));
				}
				//6.1??????????????????
				Set<Integer> roleSet = this.getRoleIdsByUser(suser);
				suser.setRoleIds(roleSet);
				//6.2????????????
				SysUserMethods userMethodsList =  this.getSysUserMethodsByUid(userInfoTmp.getPrimaryKey());
				suser.setSysUserMethodsList(userMethodsList);
				
				// ??????????????????
				ids = this.getMethodIdsByRoleIds(roleSet, userMethodsList);
			}
		}
		return ids;
	}

	// ????????????ids??????????????????
	public List<SysMethodInfo> getSysmethodInfoListByIds(Set<String> methodIds) {
		List<SysMethodInfo> list = new ArrayList<SysMethodInfo>();
		Iterator<String> it = methodIds.iterator();
		String ids = "";
		while (it.hasNext()) {
			String elem = (String) it.next();
			ids += "'" + elem + "',";
		}
		if (ids != null && ids.length() > 0) {
			list = sysMethodInfoDao.findByHqlWhere(" and model.primaryKey in ( " + ids.substring(0, ids.length() - 1) + " ) and model.isAction = " + EnumUtil.SYS_ISACTION.Vaild.value);
		}
		return list;
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????
	 * 
	 * @param roleSet
	 *            ????????????set??????
	 * @param userMethodDetail
	 *            ??????????????????
	 * @param type
	 *            ???????????? 2??????????????????????????? ?????????0??????????????????
	 * @return
	 */
	public Set<String> getMethodIdsByRoleIds(Set<Integer> roleSet, SysUserMethods userMethodDetail) {
		Set<String> methodIds = new HashSet<String>();
		Iterator<Integer> roleIt = roleSet.iterator();
		String roleIds = "";
		while (roleIt.hasNext()) {
			Integer elem = (Integer) roleIt.next();
			roleIds += elem + ",";
		}
		if (roleIds != null && roleIds.length() > 0) {
			String tmpstr = " and model.roleId in ( " + roleIds.substring(0, roleIds.length() - 1) + " )";
			List<SysRoleDetail> roleDetailList = roledetailDao.findByHqlWhere(tmpstr);
			if (roleDetailList.size() > 0) {
				for (SysRoleDetail sysRoleDetail : roleDetailList) {
					methodIds.add(sysRoleDetail.getMethodId());
				}
			}
		}
		if (userMethodDetail != null && userMethodDetail.getUserMethodDetail()!=null&&userMethodDetail.getUserMethodDetail().length()>0) {
			String[] tmps = userMethodDetail.getUserMethodDetail().trim().split(",");
			for (String str : tmps) {
				if (str!=null&&str.length() > 0) {
					methodIds.add(str);
				}
			}
		}
		return methodIds;
	}
	
	public Set<Integer> getBtnIdsByRoleIds(Set<Integer> roleSet, SysUserBtns userBtns) {
		Set<Integer> btnIds = new HashSet<Integer>();
		Iterator<Integer> roleIt = roleSet.iterator();
		String roleIds = "";
		while (roleIt.hasNext()) {
			Integer elem = (Integer) roleIt.next();
			roleIds += elem + ",";
		}
		if (roleIds != null && roleIds.length() > 0) {
			String tmpstr = " and model.roleId in ( " + roleIds.substring(0, roleIds.length() - 1) + " )";
			List<SysRoleBtn> roleBtnList = roleBtnDao.findByHqlWhere(tmpstr);
			if (roleBtnList.size() > 0) {
				for (SysRoleBtn sysRoleBtn : roleBtnList) {
					btnIds.add(sysRoleBtn.getBtnId());
				}
			}
		}
		if (userBtns != null && userBtns.getUserBtnDetail()!=null&&userBtns.getUserBtnDetail().length()>0) {
			String[] tmps = userBtns.getUserBtnDetail().trim().split(",");
			for (String str : tmps) {
				if (str!=null&&str.length() > 0) {
					btnIds.add(Integer.valueOf(str));
				}
			}
		}
		return btnIds;
	}

	// ?????????????????????
	public List<SysUserGroupDetail> getGroupListByUserId(int userId) {
		List<SysUserGroupDetail> list = usergroupdetailDao.findByHqlWhere(" and model.userId = " + userId);
		return list;
	}

	// ?????????????????????????????????
	public Set<Integer> getRoleIdsByUser(SessionUser user) {
		Set<Integer> roleIdSet = new HashSet<Integer>();
		// ????????????
		List<SysRoleBind> roleList_User = this.getRoleBingListByType("'" + user.getUserInfo().getPrimaryKey() + "'", EnumUtil.SYS_ROLE_BIND_TYPE.BIND_USER.value);
		for (SysRoleBind sysRoleBinduser : roleList_User) {
			roleIdSet.add(sysRoleBinduser.getRoleId());
		}
		// ????????????
		List<SysRoleBind> roleList_Dept = this.getRoleBingListByType("'" + user.getDepartmentInfo().getPrimaryKey() + "'", EnumUtil.SYS_ROLE_BIND_TYPE.BIND_DEPT.value);
		for (SysRoleBind sysRoleBinddept : roleList_Dept) {
			roleIdSet.add(sysRoleBinddept.getRoleId());
		}
		// ????????????
		String postIds = "";
		if (user.getMainPost() != null) {
			postIds += "'" + user.getMainPost().getPrimaryKey() + "',";
		}
		if (user.getPartPosts() != null && user.getPartPosts().size() > 0) {
			for (int i = 0; i < user.getPartPosts().size(); i++) {
				HrmPost tmp = user.getPartPosts().get(i);
				if (tmp!=null) {
					postIds += "'" + tmp.getPrimaryKey() + "',";
				}
				
			}
		}
		if (postIds != null && postIds.length() > 0) {
			List<SysRoleBind> roleList_Post = this.getRoleBingListByType(postIds.substring(0, postIds.length() - 1), EnumUtil.SYS_ROLE_BIND_TYPE.BIND_POST.value);
			for (SysRoleBind sysRoleBindpost : roleList_Post) {
				roleIdSet.add(sysRoleBindpost.getRoleId());
			}
		}
		// ???????????????
		List<SysUserGroupDetail> groupDetailList = this.getGroupListByUserId((int) user.getUserInfo().getPrimaryKey());
		String gIds = "";
		if (groupDetailList != null && groupDetailList.size() > 0) {
			for (int i = 0; i < groupDetailList.size(); i++) {
				SysUserGroupDetail detail = groupDetailList.get(i);
				gIds += "'" + detail.getGroupId() + "',";
			}
			if (gIds != null && gIds.length() > 0) {
				List<SysRoleBind> roleList_Group = this.getRoleBingListByType(gIds.substring(0, gIds.length() - 1), EnumUtil.SYS_ROLE_BIND_TYPE.BIND_GROUP.value);
				for (SysRoleBind sysRoleBindgroup : roleList_Group) {
					roleIdSet.add(sysRoleBindgroup.getRoleId());
				}
			}
		}
		return roleIdSet;
	}

	public List<SysRoleBind> getRoleBingListByType(String values, int type) {
		List<SysRoleBind> roleList = new ArrayList<SysRoleBind>();
		if (values.length()>0) {
			roleList = rolebindDao.findByHqlWhere(" and model.bindValue in ( " + values + " ) and model.bindType =" + type);
		}
		return roleList;
	}

	/**
	 * ????????????????????????
	 */
	public SysUserMethods getSysUserMethodsByUid(long uid) {
		SysUserMethods methods=null;
		List<SysUserMethods> sysUserMethodsList = sysUserMethodsDao.findByProperty("userId", (int) uid);
		if (sysUserMethodsList.size()==1) {
			methods = sysUserMethodsList.get(0);
		}
		return methods;
	}
	
	public SysUserBtns getSysUserBtnsByUid(long uid) {
		SysUserBtns userBtns = null;
		List<SysUserBtns> sysUserBtnsList = sysUserBtnsDao.findByProperty("userId", (int) uid);
		if (sysUserBtnsList.size()==1) {
			userBtns = sysUserBtnsList.get(0);
		}
		return userBtns;
	}
	/**
	 * ????????????????????????(??????????????????)
	 */
	public List<SysMethodInfo> getSysMethodInfoByCodeUnit(String upCode) {
		List<SysMethodInfo> sysMethodInfoList = sysMethodInfoDao.findByHqlWhere(SystemPack.packSysMethodInfoByTree(upCode));
		return sysMethodInfoList;
	}
	
	public int getSysMethodInfoByCodeUnitCount(String upCode) {
		 return sysMethodInfoDao.findByHqlWhereCount(SystemPack.packSysMethodInfoByTree(upCode));
	}

	
	/**
	 * ?????????????????????????????????????????????
	 * @param code
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysMethodInfo> getSysMethodInfoListByCode(String code,HttpServletRequest request){
		SessionUser user = (SessionUser) LoginContext.getSessionValueByLogin(request);
		List<SysMethodInfo> methodList = new ArrayList<SysMethodInfo>();
		if (user.getCompanyInfo().getCompanyInfoType() == EnumUtil.SYS_COMPANY_TYPE.SYSTEM.value) {//????????????
			methodList = this.getSysMethodInfoByCodeUnit(code);
		}else{
			//????????????
			//?????????????????????
			if (user.getUserInfo().getUserType() == EnumUtil.SYS_USER_TYPE.SYSTEM.value) {
				methodList = this.getSysMethodInfoByCodeUnit(code);//??????????????????
			}else{
				//??????????????????
				Set<String> ids = user.getUserMethodsSet();
				Iterator<String> it = ids.iterator();
				List<SysMethodInfo> tmplist = this.getSysMethodInfoByCodeUnit(code);
				while (it.hasNext()) {
					String str = (String) it.next();
					for (SysMethodInfo sysMethodInfo : tmplist) {
						if (sysMethodInfo.getPrimaryKey().equalsIgnoreCase(str)) {
							methodList.add(sysMethodInfo);
							break;
						}
					}
				}
			}
		}
		CaseInsensitiveComparator comp =new CaseInsensitiveComparator();
		Collections.sort(methodList,comp);
		return methodList;
	}
	
	public int getSysMethodInfoListByCodeCount(String code,HttpServletRequest request){
		SessionUser user = (SessionUser) LoginContext.getSessionValueByLogin(request);
		int count =0;
		if (user.getCompanyInfo().getCompanyInfoType() == EnumUtil.SYS_COMPANY_TYPE.SYSTEM.value) {//????????????
			count = this.getSysMethodInfoByCodeUnitCount(code);
		}else{//????????????
			if (user.getUserInfo().getUserType() == EnumUtil.SYS_USER_TYPE.SYSTEM.value) {
				count = this.getSysMethodInfoByCodeUnitCount(code);
			}else{
				//??????????????????
				boolean bl = false;
				Set<String> ids = user.getUserMethodsSet();
				Iterator<String> it = ids.iterator();
				List<SysMethodInfo> tmplist = this.getSysMethodInfoByCodeUnit(code);
				while (it.hasNext()) {
					String str = (String) it.next();
					for (SysMethodInfo sysMethodInfo : tmplist) {
						if (sysMethodInfo.getPrimaryKey().equalsIgnoreCase(str)) {
							bl = true;
							count = 1;
							break;
						}
					}
					if (bl) {
						break;
					}
				}
			}
		}
		return count;
	}
	
	// ????????????????????????
	@SuppressWarnings("rawtypes")
	class CaseInsensitiveComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			SysMethodInfo method1 = (SysMethodInfo) arg0;
			SysMethodInfo method2 = (SysMethodInfo) arg1;
			if (method1.getMethodNo()!= null &&method2.getMethodNo()!= null&& method1.getMethodNo()!=method2.getMethodNo()) {
				int m1 = method1.getMethodNo();
				int m2 = method2.getMethodNo();
				if (m1 < m2) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return 0;
			}
		}
	}
	
}
