public class Main {
    public static void main(String[] args) throws Exception {
        int arrayLength = 10000; // Длина массива
        int threadCount = 10; // Количество потоков
        int[] array = new int[arrayLength]; // Массив чисел
        int sum = 0; // Сумма чисел в последовательном вычислении
        int sumThread = 0; // Сумма чисел в параллельном вычислении с использованием потоков
        int sumFork; // Сумма чисел в параллельном вычислении с использованием ForkJoin
        Random rd = new Random(); // Генератор случайных чисел
        CountDownLatch countDownLatch = new CountDownLatch(threadCount); // Объект для синхронизации потоков
        SumThread[] threads = new SumThread[threadCount]; // Массив потоков

        // Заполнение массива случайными числами
        for (int i = 0; i < array.length; i++) {
            array[i] = rd.nextInt(10);
        }

        // Последовательное вычисление суммы
        long timeSequence = System.currentTimeMillis(); // Время начала вычислений
        long memorySeq = Runtime.getRuntime().freeMemory(); // Использование памяти перед вычислениями
        for (int value : array) {
            sum += value;
            Thread.sleep(1); // Имитация нагрузки
        }
        timeSequence = System.currentTimeMillis() - timeSequence; // Вычисление времени выполнения
        memorySeq = memorySeq - Runtime.getRuntime().freeMemory(); // Вычисление использования памяти

        // Вычисление суммы с использованием потоков
        long timeThread = System.currentTimeMillis(); // Время начала вычислений
        long memoryThr = Runtime.getRuntime().totalMemory(); // Использование памяти перед вычислениями

        // Создание и запуск потоков
        for (int i = 0; i < threadCount; i++){
            threads[i] = new SumThread(countDownLatch, Arrays.copyOfRange(
                    array,
                    i * arrayLength / threadCount,
                    (i + 1) * arrayLength / threadCount
            ));
        }

        for (int i = 0; i < threadCount; i++){
            threads[i].start();
        }

        // Ожидание завершения работы всех потоков
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // Получение суммы из каждого потока и их сложение
        for (int i = 0; i < threadCount; i++){
            sumThread += threads[i].getSum();
        }

        timeThread = System.currentTimeMillis() - timeThread; // Вычисление времени выполнения
        memoryThr = memoryThr - Runtime.getRuntime().freeMemory(); // Вычисление использования памяти

        ForkJoinPool fjp = new ForkJoinPool(); // Пул потоков для выполнения задач
        ArraySum task = new ArraySum(array, 0, array.length); // Задача для вычисления суммы массива

        // Вычисление суммы с использованием ForkJoin
        long timeFork = System.currentTimeMillis(); // Время начала вычислений
        long memoryFork = Runtime.getRuntime().freeMemory(); // Использование памяти перед вычислениями
        sumFork = fjp.invoke(task);
        timeFork = System.currentTimeMillis() - timeFork; // Вычисление времени выполнения
        memoryFork = memoryFork - Runtime.getRuntime().freeMemory(); // Вычисление использования памяти

        // Вывод результатов
        System.out.println("Sum: " + sum + ". Time: " + timeSequence + ". Memory: " + memorySeq + " [Sequence]");
        System.out.println("Sum: " + sumThread + ". Time: " + timeThread+ ". Memory: " + memoryThr  + " [Thread]");
        System.out.println("Sum: " + sumFork + ". Time: " + timeFork + ". Memory: " + memoryFork + " [Fork]");
    }
}

// Класс SumThread, наследующийся от класса Thread для вычисления суммы элементов массива в отдельном потоке
class SumThread extends Thread{
    private final int[] array; // Массив чисел для вычисления суммы
    private int sum = 0; // Сумма чисел
    private final CountDownLatch countDownLatch; // Объект для синхронизации потоков

    SumThread(CountDownLatch countDownLatch, int[] array){
        super();
        this.array = array; // Инициализация массива
        this.countDownLatch = countDownLatch; // Инициализация счетчика для синхронизации
    }

    @Override
    public void run(){
        // Вычисление суммы элементов массива
        for (int value : this.array) {
            this.sum += value;
            try {
                Thread.sleep(1); // Имитация нагрузки
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        countDownLatch.countDown(); // Уменьшение счетчика синхронизации на 1
    }

    public int getSum(){
        return this.sum; // Возвращает сумму элементов массива
    }
}

// Класс ArraySum, наследующийся от класса RecursiveTask<Integer> для вычисления суммы элементов массива с использованием ForkJoin
class ArraySum extends RecursiveTask<Integer> {
    int[] array; // Массив чисел для вычисления суммы
    int start, end; // Границы диапазона элементов массива

    public ArraySum(int[] array, int start, int end) {
        this.array = array; // Инициализация массива
        this.start = start; // Инициализация начального индекса
        this.end = end; // Инициализация конечного индекса
    }

    @Override
    protected Integer compute() {
        if (end - start <= 1) {
            return array[start]; // Если диапазон содержит один элемент, возвращаем его значение
        } else {
            int mid = (start + end) / 2; // Находим середину диапазона

            ArraySum left = new ArraySum(array, start, mid); // Создаем новую задачу для левого поддиапазона
            ArraySum right = new ArraySum(array, mid, end); // Создаем новую задачу для правого поддиапазона

            left.fork(); // Запускаем выполнение задач для левого поддиапазона в отдельном потоке
            right.fork(); // Запускаем выполнение задач для правого поддиапазона в отдельном потоке

            try {
                Thread.sleep(1); // Имитация нагрузки
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return left.join() + right.join(); // Возвращаем сумму значений из левого и правого поддиапазонов
        }
    }
}
