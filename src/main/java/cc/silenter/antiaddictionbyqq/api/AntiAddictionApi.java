package cc.silenter.antiaddictionbyqq.api;

import cc.silenter.antiaddictionbyqq.Storage;

/**
 * 获取防沉迷状态的API
 */
public class AntiAddictionApi {
    /**
     * 获取是否是成年人（注意检查是否检测过）
     * @param UUID 要获取玩家的UUID
     * @return 是否是成年人
     */
    public boolean getIsAdult(String UUID){return Storage.isAdult.get(UUID);}

    /**
     * 获取是否检测过
     * @param UUID 要获取玩家的UUID
     * @return 是否检测过
     */
    public boolean getIsChecked(String UUID){return Storage.isChecked.get(UUID);}

    /**
     * 获取是否检测中
     * @param UUID 要获取玩家的UUID
     * @return 是否检测中
     */
    public boolean getIsChecking(String UUID){return Storage.isChecking.get(UUID);}

    /**
     * 获取剩余的检测时间
     * @param UUID 要获取玩家的UUID
     * @return 时间（单位秒）
     */
    public int getTimeLeft(String UUID){return Storage.time.get(UUID);}
}
