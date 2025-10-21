package ru.savka.webinar11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Webinar11Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Webinar11Application.class, args);
		// Добавляем хук завершения работы для ForkJoinPool
		context.registerShutdownHook();
	}

	@Bean
	public CommandLineRunner commandLineRunner(ForkJoinService forkJoinService) {
		return args -> {
			System.out.println("Запуск приложения для подсчета суммы элементов с использованием ForkJoin.");

			// Генерируем большой список чисел
			List<Integer> numbers = new ArrayList<>();
			Random random = new Random();
			for (int i = 0; i < 1000000; i++) { // 1 миллион элементов
				numbers.add(random.nextInt(100)); // Числа от 0 до 99
			}

			int threshold = 10000; // Порог для разбиения задач

			long startTime = System.currentTimeMillis();
			long sum = forkJoinService.calculateSum(numbers, threshold);
			long endTime = System.currentTimeMillis();

			System.out.println("Общая сумма элементов: " + sum);
			System.out.println("Время выполнения (ForkJoin): " + (endTime - startTime) + " мс");

			// Синхронная версия для сравнения
			long syncStartTime = System.currentTimeMillis();
			long syncSum = numbers.stream().mapToLong(Integer::longValue).sum();
			long syncEndTime = System.currentTimeMillis();
			System.out.println("Общая сумма элементов (синхронно): " + syncSum);
			System.out.println("Время выполнения (синхронно): " + (syncEndTime - syncStartTime) + " мс");

			forkJoinService.shutdown(); // Завершаем работу ForkJoinPool
		};
	}
}
