package lottery.domains.content.vo.user;

import lottery.domains.content.entity.Lottery;
import lottery.domains.content.entity.LotteryPlayRules;
import lottery.domains.content.entity.LotteryPlayRulesGroup;
import lottery.domains.content.entity.UserBets;
import lottery.domains.pool.LotteryDataFactory;

public class UserBetsVO
{
  private String username;
  private String lottery;
  private String mname;
  private UserBets bean;
  
  public UserBetsVO(UserBets bean, LotteryDataFactory lotteryDataFactory, boolean isShowNum)
  {
    this.bean = bean;
    UserVO tmpUser = lotteryDataFactory.getUser(bean.getUserId());
    if (tmpUser != null) {
      this.username = tmpUser.getUsername();
    }
    Lottery lottery = lotteryDataFactory.getLottery(bean.getLotteryId());
    if (lottery != null) {
      this.lottery = lottery.getShowName();
    }
    LotteryPlayRules playRules = lotteryDataFactory.getLotteryPlayRules(bean.getRuleId());
    if (playRules != null)
    {
      LotteryPlayRulesGroup group = lotteryDataFactory.getLotteryPlayRulesGroup(playRules.getGroupId());
      if (group != null) {
        this.mname = ("[" + group.getName() + "_" + playRules.getName() + "]");
      }
    }
    if (!isShowNum) {
      this.bean.setCodes(null);
    }
  }
  
  public String getUsername()
  {
    return this.username;
  }
  
  public void setUsername(String username)
  {
    this.username = username;
  }
  
  public String getLottery()
  {
    return this.lottery;
  }
  
  public void setLottery(String lottery)
  {
    this.lottery = lottery;
  }
  
  public String getMname()
  {
    return this.mname;
  }
  
  public void setMname(String mname)
  {
    this.mname = mname;
  }
  
  public UserBets getBean()
  {
    return this.bean;
  }
  
  public void setBean(UserBets bean)
  {
    this.bean = bean;
  }
}
