package cn.yznu.XXX.util;

/**
 * 质数相关的工具类
 * 提供质数判断和哥德巴赫猜想分解功能
 *
 * @author YourName
 * @version 1.0
 */
public class PrimeUtil {

    /**
     * 判断一个数是否为质数
     *
     * @param num 待判断的整数
     * @return true-是质数，false-不是质数
     */
    public static boolean isPrime(int num) {
        // 小于等于1的数不是质数
        if (num <= 1) {
            return false;
        }
        // 2是质数
        if (num == 2) {
            return true;
        }
        // 偶数不是质数（除了2）
        if (num % 2 == 0) {
            return false;
        }

        // 只需要检查到平方根即可
        // 从3开始，每次+2，跳过偶数
        int limit = (int) Math.sqrt(num);
        for (int i = 3; i <= limit; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证哥德巴赫猜想：找出两个质数，使其和等于给定的偶数
     *
     * @param n 待分解的偶数（必须大于2）
     * @return 包含两个质数的数组，如果找不到则返回null
     */
    public static int[] findTwoPrimesForGoldbach(int n) {
        // 参数校验
        if (n <= 2 || n % 2 != 0) {
            return null;
        }

        // 从小到大查找质数对
        // 只需要查找到n/2，避免重复
        for (int i = 2; i <= n / 2; i++) {
            if (isPrime(i) && isPrime(n - i)) {
                return new int[]{i, n - i};
            }
        }
        return null;
    }

    /**
     * 验证输入的偶数是否有效（大于100且为偶数）
     *
     * @param n 待验证的数字
     * @return true-有效，false-无效
     */
    public static boolean isValidEvenNumber(int n) {
        return n > 100 && n % 2 == 0;
    }
}