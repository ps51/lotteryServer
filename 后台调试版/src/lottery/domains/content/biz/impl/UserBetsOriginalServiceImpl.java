package lottery.domains.content.biz.impl;

import java.util.ArrayList;
import java.util.List;
import javautils.StringUtil;
import javautils.jdbc.PageList;
import lottery.domains.content.biz.UserBetsOriginalService;
import lottery.domains.content.dao.UserBetsOriginalDao;
import lottery.domains.content.dao.UserDao;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBetsOriginal;
import lottery.domains.content.vo.user.UserBetsOriginalVO;
import lottery.domains.pool.LotteryDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBetsOriginalServiceImpl
  implements UserBetsOriginalService
{
  @Autowired
  private UserDao uDao;
  @Autowired
  private UserBetsOriginalDao uBetsOriginalDao;
  @Autowired
  private LotteryDataFactory lotteryDataFactory;
  
  public PageList search(String keyword, String username, Integer utype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime, String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status, int start, int limit)
  {
    boolean isSearch = true;
    StringBuilder sqlStr = new StringBuilder();
    if (StringUtil.isNotNull(username))
    {
      User user = this.uDao.getByUsername(username);
      if (user != null) {
        sqlStr.append(" and b.user_id = ").append(user.getId());
      } else {
        isSearch = false;
      }
    }
    if (StringUtil.isNotNull(keyword)) {
      if (StringUtil.isInteger(keyword)) {
        sqlStr.append("\tand b.id = ").append(Integer.parseInt(keyword));
      } else {
        sqlStr.append("\tand b.billno = ").append(keyword);
      }
    }
    if (type != null) {
      sqlStr.append("\tand b.type = ").append(type.intValue());
    }
    if (lotteryId != null) {
      sqlStr.append("\tand b.lottery_id = ").append(lotteryId.intValue());
    }
    if (StringUtil.isNotNull(expect)) {
      sqlStr.append("\tand b.expect = ").append(expect);
    }
    if (ruleId != null) {
      sqlStr.append("\tand b.rule_id = ").append(ruleId);
    }
    if (StringUtil.isNotNull(minTime)) {
      sqlStr.append("  and b.time > ").append("'" + minTime + "'");
    }
    if (StringUtil.isNotNull(maxTime)) {
      sqlStr.append("  and b.time < ").append("'" + maxTime + "'");
    }
    if (StringUtil.isNotNull(minPrizeTime)) {
      sqlStr.append("  and b.prize_time > ").append("'" + minPrizeTime + "'");
    }
    if (StringUtil.isNotNull(maxPrizeTime)) {
      sqlStr.append("  and b.prize_time < ").append("'" + maxPrizeTime + "'");
    }
    if (minMoney != null) {
      sqlStr.append("  and b.money >= ").append(minMoney.doubleValue());
    }
    if (maxMoney != null) {
      sqlStr.append("  and b.money <= ").append(maxMoney.doubleValue());
    }
    if (minMultiple != null) {
      sqlStr.append("  and b.multiple >= ").append(minMultiple.doubleValue());
    }
    if (maxMultiple != null) {
      sqlStr.append("  and b.multiple <= ").append(maxMultiple.doubleValue());
    }
    if (minPrizeMoney != null) {
      sqlStr.append("  and b.prize_money >= ").append(minPrizeMoney.doubleValue());
    }
    if (maxPrizeMoney != null) {
      sqlStr.append("  and b.prize_money >= ").append(maxPrizeMoney.doubleValue());
    }
    if (status != null) {
      sqlStr.append("  and b.status = ").append(status.intValue());
    }
    if (utype != null) {
      sqlStr.append("  and u.type = ").append(utype);
    } else {
      sqlStr.append("  and u.upid != ").append(0);
    }
    sqlStr.append("  order by  b.id  desc  ");
    if (isSearch)
    {
      List<UserBetsOriginalVO> list = new ArrayList();
      PageList pList = this.uBetsOriginalDao.find(sqlStr.toString(), start, limit);
      for (Object tmpBean : pList.getList())
      {
        UserBetsOriginalVO tmpVO = new UserBetsOriginalVO((UserBetsOriginal)tmpBean, this.lotteryDataFactory, false);
        list.add(tmpVO);
      }
      pList.setList(list);
      return pList;
    }
    return null;
  }
  
  public UserBetsOriginalVO getById(int id)
  {
    UserBetsOriginal entity = this.uBetsOriginalDao.getById(id);
    if (entity != null)
    {
      UserBetsOriginalVO bean = new UserBetsOriginalVO(entity, this.lotteryDataFactory, true);
      return bean;
    }
    return null;
  }
  
  public double[] getTotalMoney(String keyword, String username, Integer utype, Integer type, Integer lotteryId, String expect, Integer ruleId, String minTime, String maxTime, String minPrizeTime, String maxPrizeTime, Double minMoney, Double maxMoney, Integer minMultiple, Integer maxMultiple, Double minPrizeMoney, Double maxPrizeMoney, Integer status)
  {
    Integer userId = null;
    if (StringUtil.isNotNull(username))
    {
      User user = this.uDao.getByUsername(username);
      if (user != null) {
        userId = Integer.valueOf(user.getId());
      }
    }
    return this.uBetsOriginalDao.getTotalMoney(keyword, userId, utype, type, lotteryId, expect, ruleId, minTime, maxTime, minPrizeTime, maxPrizeTime, minMoney, maxMoney, minMultiple, maxMultiple, minPrizeMoney, maxPrizeMoney, status);
  }
}
