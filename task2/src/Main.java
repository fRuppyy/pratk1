import java.util.Scanner;
import java.lang.Thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(2); // Создание пула потоков с фиксированным количеством (2) потоков
        Scanner scan = new Scanner(System.in); // Создание объекта сканера для чтения ввода с консоли

        int input1 = Integer.parseInt(scan.nextLine()); // Чтение целочисленного значения с консоли
        Future<?> ftr = es.submit(new MyRunnable(input1)); // Подача задачи на выполнение в пуле потоков и сохранение Future-объекта для получения результата

        while (true) {
            String input2 = scan.nextLine(); // Чтение строки с консоли
            if (!ftr.isDone()) { // Проверка, завершилась ли предыдущая задача
                es.submit(new MyRunnable(Integer.parseInt(input2))); // Подача новой задачи на выполнение в пуле потоков
            }
            break; // Выход из цикла
        }

        es.shutdown(); // Завершение работы пула потоков
    }
}

class MyRunnable implements Runnable {
    Integer number; // Число для выполнения операции возведения в квадрат
    public MyRunnable(int number) {
        this.number = number; // Инициализация числа
    }

    @Override
    public void run() {
        try {
            String threadName = Thread.currentThread().getName(); // Получение имени текущего потока

            System.out.println("["+threadName+"] Waiting for response... [Math.pow("+number+",2)]"); // Вывод сообщения ожидания ответа

            long random = (long)(Math.random() * 4000) + 1000; // Генерация случайного числа для имитации задержки
            double result = Math.pow(number, 2); // Выполнение операции возведения в квадрат

            Thread.sleep(random); // Имитация работы потока

            System.out.println(
                    "["+threadName+"]" +
                            " Square of the number " + number + " is: " + result +
                            " [response time: " + random + "ms]"
            ); // Вывод результата работы потока
        } catch (Exception ex) {
            ex.printStackTrace(System.out); // Печать стека ошибок при исключении
        }
    }
}
