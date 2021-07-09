package com.slodon.b2b2c.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 微信红包随机金额算法
 */
public class RedBagUtils {

    private static final Random random = new Random();

    public static void main(String[] args) {
//        int i = 0;
//        //打印100个测试概率的准确性
//        for (i = 0; i <= 100; i++) {
//            List<Double> lowAmount = new ArrayList<>();
//            List<Double> highAmount = new ArrayList<>();
//            List<Double> percent = new ArrayList<>();
//            lowAmount.add(0.01);
//            lowAmount.add(10.0);
//            lowAmount.add(20.0);
//            lowAmount.add(30.0);
//            lowAmount.add(40.0);
//            highAmount.add(10.0);
//            highAmount.add(20.0);
//            highAmount.add(30.0);
//            highAmount.add(40.0);
//            highAmount.add(50.0);
//            percent.add(0.50);
//            percent.add(0.30);
//            percent.add(0.15);
//            percent.add(0.04);
//            percent.add(0.01);
//            double result = getPercentageRandom(lowAmount, highAmount, percent);
//            System.out.println(result);
//        }

//        System.out.println(genRandList(500, 20, 1, 200));

        BigDecimal b1 = BigDecimal.valueOf(1.00);
        BigDecimal b2 = BigDecimal.valueOf(188.00);
        System.out.println(createRandomKey(b1, b2));
    }

    /**
     * 根据总数分割个数及限定区间进行数据随机处理
     * 数列浮动阀值为0.85
     * @param total    - 被分割的总数
     * @param splitNum - 分割的个数
     * @param min      - 单个数字下限
     * @param max      - 单个数字上限
     * @return - 返回符合要求的数字列表
     */
    public static List<Integer> genRandList(int total, int splitNum, int min, int max) {
        return genRandList(total, splitNum, min, max, 0.85f);
    }

    /**
     * 根据总数分割个数及限定区间进行数据随机处理
     * @param total    - 被分割的总数
     * @param splitNum - 分割的个数
     * @param min      - 单个数字下限
     * @param max      - 单个数字上限
     * @param thresh   - 数列浮动阀值[0.0, 1.0]
     * @return
     */
    public static List<Integer> genRandList(int total, int splitNum, int min, int max, float thresh) {
        assert total >= splitNum * min && total <= splitNum * max;
        assert thresh >= 0.0f && thresh <= 1.0f;
        // 平均分配
        int average = total / splitNum;
        List<Integer> list = new ArrayList<>(splitNum);
        int rest = total - average * splitNum;
        for (int i = 0; i < splitNum; i++) {
            if (i < rest) {
                list.add(average + 1);
            } else {
                list.add(average);
            }
        }
        // 如果浮动阀值为0则不进行数据随机处理
        if (thresh == 0) {
            return list;
        }
        // 根据阀值进行数据随机处理
        for (int i = 0; i < splitNum - 1; i++) {
            int nextIndex = i + 1;
            int itemThis = list.get(i);
            int itemNext = list.get(nextIndex);
            boolean isLt = itemThis < itemNext;
            int rangeThis = isLt ? max - itemThis : itemThis - min;
            int rangeNext = isLt ? itemNext - min : max - itemNext;
            int rangeFinal = (int) Math.ceil(thresh * (Math.min(rangeThis, rangeNext) + 1));
            int randOfRange = random.nextInt(rangeFinal);
            int randRom = isLt ? 1 : -1;
            list.set(i, list.get(i) + randRom * randOfRange);
            list.set(nextIndex, list.get(nextIndex) + randRom * randOfRange * -1);
        }
        return list;
    }

    /**
     * 创建一个在范围内的随机数
     * @param lowAmount
     * @param highAmount
     * @return
     */
    public static BigDecimal createRandomKey(BigDecimal lowAmount, BigDecimal highAmount) {
        BigDecimal randomNumber, result = BigDecimal.ZERO;
        //生成随机数
        randomNumber = BigDecimal.valueOf(Math.random());
        //随机生成结果
        result = randomNumber.multiply(highAmount.subtract(lowAmount)).add(lowAmount);
        //保留两位小数,四舍五入
        return result.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 按指定概率随机立减
     * @param lowAmount  最低值
     * @param highAmount 最高值
     * @param percent    概率
     * @return
     */
    public static double getPercentageRandom(List<Double> lowAmount, List<Double> highAmount, List<Double> percent) {
        double randomNumber, result = 0;
        //生成随机数
        randomNumber = Math.random();
        double lowRate, highRate = 0;
        //遍历每一个区间
        for (int i = 0; i < percent.size(); i++) {
            lowRate = highRate;
            highRate += percent.get(i);
            if (randomNumber >= lowRate && randomNumber <= highRate) {
                //如果第一次生成的随机数在这个区间，再次生成随机数
                randomNumber = Math.random();
                //根据第二次生成的随机数（大于0小于1）在这个区间内取一个值，即为随机生成结果
                result = randomNumber * (highAmount.get(i) - lowAmount.get(i)) + lowAmount.get(i);
                break;
            }
        }
        BigDecimal bg = new BigDecimal(result);
        //保留两位小数,四舍五入
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
