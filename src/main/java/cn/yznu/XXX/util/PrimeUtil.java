package cn.yznu.XXX.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 质数相关的工具类
 * 提供质数判断和哥德巴赫猜想分解功能
 *
 * @author YourName
 * @version 2.0
 */
public class PrimeUtil {

    // 缓存已计算过的质数，提高重复查询效率
    private static final List<Integer> primeCache = new ArrayList<>();

    static {
        // 初始化缓存，添加最小的质数2
        primeCache.add(2);
    }

    /**
     * 判断一个数是否为质数（使用缓存优化）
     *
     * @param num 待判断的整数
     * @return true-是质数，false-不是质数
     */
    public static boolean isPrime(int num) {
        // 小于等于1的数不是质数
        if (num <= 1) {
            return false;
        }

        // 检查缓存中是否已存在
        if (num <= primeCache.get(primeCache.size() - 1)) {
            return primeCache.contains(num);
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
        int limit = (int) Math.sqrt(num);

        // 先检查已缓存的质数
        for (int prime : primeCache) {
            if (prime > limit) {
                break;
            }
            if (num % prime == 0) {
                return false;
            }
        }

        // 继续检查未缓存的奇数
        int start = primeCache.get(primeCache.size() - 1) + 2;
        for (int i = start; i <= limit; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }

        // 如果是质数，加入缓存
        primeCache.add(num);
        return true;
    }

    /**
     * 使用埃拉托斯特尼筛法快速查找所有小于等于n的质数
     * 适用于需要批量查找质数的场景
     *
     * @param n 上限值
     * @return 布尔数组，true表示索引对应的数是质数
     */
    public static boolean[] sieveOfEratosthenes(int n) {
        if (n < 2) {
            return new boolean[0];
        }

        boolean[] isPrime = new boolean[n + 1];
        // 初始化所有数为质数
        for (int i = 2; i <= n; i++) {
            isPrime[i] = true;
        }

        // 筛除非质数
        int limit = (int) Math.sqrt(n);
        for (int i = 2; i <= limit; i++) {
            if (isPrime[i]) {
                // 从i*i开始标记，因为更小的倍数已经被之前的质数标记过了
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        return isPrime;
    }

    /**
     * 验证哥德巴赫猜想：找出两个质数，使其和等于给定的偶数
     * 使用优化算法：
     * 1. 从小质数开始查找，找到第一对即返回
     * 2. 对于大数，使用筛法预处理质数表
     *
     * @param n 待分解的偶数（必须大于2）
     * @return 包含两个质数的数组，如果找不到则返回null
     */
    public static int[] findTwoPrimesForGoldbach(int n) {
        // 参数校验
        if (n <= 2 || n % 2 != 0) {
            return null;
        }

        // 根据n的大小选择不同的策略
        // 对于小于100万的数，使用简单遍历（已足够快）
        if (n < 1_000_000) {
            return findTwoPrimesByTraversal(n);
        }
        // 对于大数，使用筛法优化
        else {
            return findTwoPrimesBySieve(n);
        }
    }

    /**
     * 通过遍历方式查找两个质数（适用于中等大小的数）
     * 从2开始查找，找到第一个匹配的质数对即返回
     *
     * @param n 目标偶数
     * @return 质数对数组
     */
    private static int[] findTwoPrimesByTraversal(int n) {
        // 只检查到n/2，避免重复
        for (int i = 2; i <= n / 2; i++) {
            // 优化：如果i是偶数且大于2，跳过（因为偶数除了2都不是质数）
            if (i > 2 && i % 2 == 0) {
                continue;
            }

            if (isPrime(i) && isPrime(n - i)) {
                return new int[]{i, n - i};
            }
        }
        return null;
    }

    /**
     * 通过筛法查找两个质数（适用于大数）
     * 预先计算所有小于n的质数，提高查找效率
     *
     * @param n 目标偶数
     * @return 质数对数组
     */
    private static int[] findTwoPrimesBySieve(int n) {
        // 使用筛法获取所有小于n的质数
        boolean[] isPrime = sieveOfEratosthenes(n);

        // 查找质数对
        for (int i = 2; i <= n / 2; i++) {
            // 优化：如果i是偶数且大于2，跳过
            if (i > 2 && i % 2 == 0) {
                continue;
            }

            if (isPrime[i] && isPrime[n - i]) {
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

    /**
     * 获取质数缓存大小（用于调试）
     *
     * @return 缓存中的质数数量
     */
    public static int getPrimeCacheSize() {
        return primeCache.size();
    }
}