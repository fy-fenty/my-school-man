package org.ymm.services.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.ymm.services.IManagerService;
import org.ymm.services.ISystemService;
import org.zjf.constant.AppConstant;
import org.zjf.dao.IDispatchDetailDao;
import org.zjf.dao.IDispatchListDao;
import org.zjf.dao.IDispatchResultDao;
import org.zjf.dao.ILoginUserDao;
import org.zjf.dao.ISysEmployeeDao;
import org.zjf.entity.DispatchList;
import org.zjf.entity.DispatchResult;
import org.zjf.entity.SysEmployee;
import org.zjf.entity.SysPositions;
import org.zjf.exception.MyException;
import org.zjf.util.MD5;
import org.zjf.util.StringUtil;
import org.zjf.vo.BaseVo;
import org.zjf.vo.DispatchListVo;
import org.zjf.vo.DispatchResultVo;
import org.zjf.vo.Page;
import org.zjf.vo.Result;
import org.zjf.vo.SysEmployeeVo;

public class ManagerServiceImpl implements IManagerService {

	private IDispatchListDao iDispatchListDao;
	private ISysEmployeeDao iSysEmployeeDao;
	private ILoginUserDao iLoginUserDao;
	private ISystemService iSystemService;
	private IDispatchDetailDao iDispatchDetailDao;
	private IDispatchResultDao iDispatchResultDao;
	
	public IDispatchResultDao getiDispatchResultDao() {
		return iDispatchResultDao;
	}

	public void setiDispatchResultDao(IDispatchResultDao iDispatchResultDao) {
		this.iDispatchResultDao = iDispatchResultDao;
	}

	public ISystemService getiSystemService() {
		return iSystemService;
	}

	public void setiSystemService(ISystemService iSystemService) {
		this.iSystemService = iSystemService;
	}

	public IDispatchDetailDao getiDispatchDetailDao() {
		return iDispatchDetailDao;
	}

	public void setiDispatchDetailDao(IDispatchDetailDao iDispatchDetailDao) {
		this.iDispatchDetailDao = iDispatchDetailDao;
	}

	public ISysEmployeeDao getiSysEmployeeDao() {
		return iSysEmployeeDao;
	}

	public void setiSysEmployeeDao(ISysEmployeeDao iSysEmployeeDao) {
		this.iSysEmployeeDao = iSysEmployeeDao;
	}

	public ILoginUserDao getiLoginUserDao() {
		return iLoginUserDao;
	}

	public void setiLoginUserDao(ILoginUserDao iLoginUserDao) {
		this.iLoginUserDao = iLoginUserDao;
	}

	public IDispatchListDao getiDispatchListDao() {
		return iDispatchListDao;
	}

	public void setiDispatchListDao(IDispatchListDao iDispatchListDao) {
		this.iDispatchListDao = iDispatchListDao;
	}

	
	public Result applyClaims(SysEmployee sysEmployee, DispatchResultVo vo)
			throws MyException {
		Result result = new Result(true, AppConstant.OTHER_ERROR, "A001");
		
		if(sysEmployee==null||vo==null){
			return new Result(true, AppConstant.OTHER_ERROR, "A003");
		}
		if(StringUtil.isEmpty(sysEmployee.getDepartmentId()+"")==false
				||StringUtil.isEmpty(sysEmployee.getPId()+"")==false){
			return new Result(true, AppConstant.OTHER_ERROR, "A003");
		}
		SysPositions sysPositions= iSystemService.findPositionById(sysEmployee.getPId());
		if(sysPositions==null){
			return new Result(true, AppConstant.OTHER_ERROR, "A003");
		}
		if(!"部门经理".equals(sysPositions.getPNameCn())){
			return new Result(true, AppConstant.OTHER_ERROR, "A005");
		}
		
		// 查询报销单状态
		
		if(StringUtil.isEmpty(vo.getSheetId()+"")==false){
			return new Result(true, AppConstant.OTHER_ERROR, "A003");
		}
		DispatchResult dr1 = iSystemService.findResultById(vo.getSheetId());
		if (dr1 == null) {
			return new Result(false, AppConstant.UPDATE_ERROR, "A013");
		}

		if(StringUtil.isEmpty(sysEmployee.getESn())==false){
			return new Result(true, AppConstant.OTHER_ERROR, "A003");
		}
		//System.out.println(dr1.getCheckNext());
		if(!dr1.getCheckNext().equals(sysEmployee.getESn())){
			return new Result(false, AppConstant.UPDATE_ERROR, "A013");
		}
		
		if(StringUtil.isEmpty(vo.getCheckStatus()+"")==false){
			return new Result(true, AppConstant.OTHER_ERROR, "A003");
		}
		
		DispatchResult saveDr2=new DispatchResult();
		saveDr2.setSheetId(dr1.getSheetId());
		saveDr2.setCheckTime(new Date());
		saveDr2.setCheckComment(vo.getCheckComment());
		saveDr2.setCheckSn(sysEmployee.getESn());
		//已审批 设置下一个审批人
		if(vo.getCheckStatus()==2){//同意
			String sql="select sum(money) from dispatch_detail where sheet_id=?";
			double  money=Double.parseDouble(iDispatchDetailDao.createSQLQuery(sql, dr1.getSheetId()).uniqueResult().toString());
			if(money>5000){
				String sq1l="from SysEmployee where department_id=? and p_id=1";
				SysEmployee se= iSysEmployeeDao.findUniqueByHQL(sq1l,sysEmployee.getDepartmentId());
				saveDr2.setCheckNext(se.getESn());
			}else {
				String sq12=" from SysEmployee where department_id=3";
				SysEmployee se= iSysEmployeeDao.findUniqueByHQL(sq12);
				saveDr2.setCheckNext(se.getESn());
			}
			saveDr2.setCheckStatus((long)2);
		}else if(vo.getCheckStatus()==4){//打回
			saveDr2.setCheckNext(dr1.getCheckSn());
			saveDr2.setCheckStatus((long)4);
		}else if(vo.getCheckStatus()==3){
			saveDr2.setCheckStatus((long)3);
		}
		else {
			return new Result(true, AppConstant.OTHER_ERROR, "A013");
		}
		
		iDispatchResultDao.save(saveDr2);//cs ok
		return result;
	}	
	
	public static void main(String[] args) throws Exception {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				new String[] { "spring-sessinfactory.xml",
						"spring-dao-beans.xml", "spring-trans.xml" });
		IManagerService ies = ac.getBean("managerServiceImpl", IManagerService.class);
		SysEmployee se=new SysEmployee();
		se.setDepartmentId((long)2);
		se.setPId((long)2);
		se.setESn("xxxx1003");
		
		DispatchResultVo vo=new DispatchResultVo();
		vo.setDrId((long)81);
		vo.setCheckStatus((long)2);
		vo.setSheetId((long)19);
		ies.applyClaims(se,vo);
		
		
		/*SysEmployee sysEmployee=new SysEmployee();
		sysEmployee.setDepartmentId((long)1);
		BaseVo bv=new BaseVo(0,100);
		System.out.println(ies.findMyDepartClaims(sysEmployee, bv).getResult().size());*/
	}
	
	
	public DispatchList getDispatchListValue(DispatchListVo dispatchListVo,SysEmployee SysEmployee){
		DispatchList dispatchList=new DispatchList();
		dispatchList.setDlId(dispatchListVo.getDlId());
		dispatchList.setESn(dispatchListVo.getESn());
		dispatchList.setCreateTime(new Timestamp(new Date().getTime()));
		dispatchList.setEventExplain(dispatchListVo.getEventExplain());
		dispatchList.setFlag(dispatchListVo.getFlag());
		return dispatchList;
	}

	//查询当前审批状态是否是新建
	public Result getDispatchResultEnd(long dl_id) throws MyException{
		DispatchResult dr1 = iSystemService.findResultById(dl_id);
		if (dr1 != null) {
			return new Result(false, AppConstant.UPDATE_ERROR, "A013");
		}
		return new Result(true, AppConstant.DEFAULT_MSG, "A001");
	}
	
	
	public Page findMyApplyClaims(SysEmployee sysEmployee, BaseVo baseVo)
			throws MyException {
		if(sysEmployee==null||baseVo==null){
			throw new MyException("A003");
		}
		
		String sql="select tt2.* from " 
				+"  (select * from dispatch_list where dl_id in  "
				+"  (select sheet_id from  dispatch_result dr1 where check_time =(select max(check_time) from dispatch_result dr2 where dr1.sheet_id=dr2.sheet_id ) "
				+"	and check_next=?) and e_sn in (select e_sn from sys_employee where department_id=?)) tt1 "
				+"  left join "
				+"  (select t1.dl_id,t1.e_name,t2.money,t1.create_time as create_time,t3.cs as disp_status from  "
				+"  (select dl.dl_id,dl.e_sn,se.e_name,dl.create_time,dl.flag from dispatch_list dl left join sys_employee se on dl.e_sn=se.e_sn) t1 "
				+"  left join "
				+"  (select dd.sheet_id, sum(money) as money from dispatch_detail dd group by dd.sheet_id) t2 "
				+"  on t1.dl_id=t2.sheet_id "
				+"  left join "
				+"  (select d1.sheet_id,(select ds1.da_status from dispatch_status ds1 where ds1.da_id=d1.check_status) as cs from dispatch_result d1 where check_time =(select max(check_time) from dispatch_result d where d.sheet_id=d1.sheet_id)) t3 "
				+"  on t3.sheet_id=t2.sheet_id ) tt2 "
				+"  on  tt1.dl_id=tt2.dl_id ";     
         
		if(StringUtil.isEmpty(sysEmployee.getDepartmentId()+"")==false){
			throw new MyException("A003");
		}
		Page page = iDispatchListDao.findPageBySql(baseVo, sql,new String[]{sysEmployee.getESn(),sysEmployee.getDepartmentId()+""});
		return page; //cs ok
	}

	
	
	public Result SetPwd(SysEmployee manger, SysEmployeeVo vo,String pwd)
			throws MyException {
		Result result = new Result(true, AppConstant.UPDATE_ERROR, "A001");
		if(manger==null||vo==null){
			return new Result(true, AppConstant.UPDATE_ERROR, "A003");
		}
		if(StringUtil.isEmpty(manger.getEId()+"")==false
				||StringUtil.isEmpty(vo.getEId()+"")==false){
			return new Result(true, AppConstant.UPDATE_ERROR, "A003");
		}
		SysEmployee manger1= iSysEmployeeDao.get(manger.getEId());
		SysEmployee sysEmp2= iSysEmployeeDao.get(vo.getEId());
		if(manger1.getPId()!=2){
			return new Result(true, AppConstant.UPDATE_ERROR, "A005");
		}
		if(!manger1.getPId().equals(vo.getPId())){
			return new Result(true, AppConstant.UPDATE_ERROR, "A012");
		}

		String sql="update login_user set u_pwd=? where E_SN=?";
		
		iLoginUserDao.createSQLQuery(sql, new String[]{vo.getESn(),MD5.MD5Encode(pwd)});
		return result;
	}

	
	public Page findMyDepartClaims(SysEmployee sysEmployee, BaseVo baseVo)
			throws MyException {
		if(sysEmployee==null||baseVo==null){
			throw new MyException("A003");
		}
		String sql="select tt2.* from "
				 +" (select * from dispatch_list where  e_sn in (select e_sn from sys_employee where department_id =?) "  
				 +" and dl_id in (select sheet_id from dispatch_result group by sheet_id)) tt1 "
				 +" left join "
				 +" (select t1.dl_id,t1.e_name,t2.money,t1.create_time as create_time,t3.cs as disp_status from "
				 +" (select dl.dl_id,dl.e_sn,se.e_name,dl.create_time,dl.flag from dispatch_list dl left join sys_employee se on dl.e_sn=se.e_sn) t1 "
				 +" left join "
				 +" (select dd.sheet_id, sum(money) as money from dispatch_detail dd group by dd.sheet_id) t2 "
				 +" on t1.dl_id=t2.sheet_id "
				 +" left join "
				 +" (select d1.sheet_id,(select ds1.da_status from dispatch_status ds1 where ds1.da_id=d1.check_status) as cs from dispatch_result d1 where check_time =(select max(check_time) from dispatch_result d where d.sheet_id=d1.sheet_id)) t3 "
				 +" on t3.sheet_id=t2.sheet_id) tt2 "
				 +" on tt1.dl_id=tt2.dl_id ";
		if(StringUtil.isEmpty(sysEmployee.getDepartmentId()+"")==false){
			throw new MyException("A003");
		}
		Page page = iDispatchListDao.findPageBySql(baseVo, sql,sysEmployee.getDepartmentId());
		return page;
	}
	
	
	
	public SysPositions loginUser(String username, String pwd)
			throws MyException {
		
		return null;
	}
}
