/**
 * 生成符合正态分布的分数
 */
class GaussianRand {

    def random
    def map = [:]

    GaussianRand(Random rand) {
        this.random = rand
    }

    int MIN = 0   // 最大值
    int MAX = 150 // 最小值
    int mean = 90 // 数学期望值
    int sd = 30   // 标注差

    int nextScore() {
        def gaussian = MIN - 1

        while(gaussian < MIN || gaussian > MAX) {
            gaussian = random.nextGaussian(); // mean 0.0, standard deviation 1.0
            // transform
            gaussian = (int)(sd * gaussian) + mean
        }

        return gaussian
    }

}