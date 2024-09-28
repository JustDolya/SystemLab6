import java.util.concurrent.atomic.AtomicInteger;

class RabbitAndTurtle {
    public static AnimalThread rabbit;
    public static AnimalThread turtle;

    public static void main(String[] args) {
        // Инициализируем потоки с одинаковыми начальными приоритетами
        rabbit = new AnimalThread("Rabbit", Thread.MAX_PRIORITY);
        turtle = new AnimalThread("Turtle", Thread.NORM_PRIORITY);

        // Запуск гонки
        rabbit.start();
        turtle.start();
    }
}

class AnimalThread extends Thread {
    private static final int FINISH_LINE = 100;
    private final AtomicInteger progress = new AtomicInteger(0); // Пройденное расстояние
    private final String racerName;

    public AnimalThread(String name, int initialPriority) {
        this.racerName = name;
        setName(name); // Устанавливаем имя потока
        setPriority(initialPriority); // Устанавливаем приоритет потока
    }

    @Override
    public void run() {
        while (progress.get() < FINISH_LINE) {
            try {
                // Время задержки, зависящее от текущего приоритета
                long delay = (11 - getPriority()) * 10L;
                Thread.sleep(delay); // Чем выше приоритет, тем меньше задержка

                // Увеличиваем прогресс
                progress.incrementAndGet();

                // Проверяем, не отстает ли участник на 10+ единиц от соперника
                if (this.racerName.equals("Turtle") && progress.get() < RabbitAndTurtle.rabbit.getProgress() - 10) {
                    boostTurtle();
                } else if (this.racerName.equals("Rabbit") && progress.get() < RabbitAndTurtle.turtle.getProgress() - 10) {
                    boostRabbit();
                }

                // Печатаем текущую трассу
                renderTrack();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(racerName + " has crossed the finish line!");
    }

    private void boostTurtle() {
        // Увеличиваем приоритет черепахи
        setPriority(Thread.MAX_PRIORITY);
        RabbitAndTurtle.rabbit.setPriority(Thread.NORM_PRIORITY);
        System.out.println("Turtle gets a speed boost!");
    }

    private void boostRabbit() {
        // Увеличиваем приоритет кролика
        setPriority(Thread.MAX_PRIORITY);
        RabbitAndTurtle.turtle.setPriority(Thread.NORM_PRIORITY);
        System.out.println("Rabbit gets a speed boost!");
    }

    public int getProgress() {
        return progress.get();
    }

    // Метод для визуализации трассы
    private void renderTrack() {
        StringBuilder track = new StringBuilder();
        for (int i = 0; i < progress.get(); i++) track.append("-");

        if (racerName.equals("Turtle")) track.append("\uD80C\uDD89"); // Черепаха
        else track.append("\uD83D\uDC30"); // Кролик

        for (int i = progress.get(); i < FINISH_LINE; i++) track.append("-");

        System.out.println(track);
    }
}