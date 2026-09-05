package com.example;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Приложение запущено");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {

            printMenu();

            while (!scanner.hasNextInt()) {
                System.out.println("Команда должна быть числом.");
                logger.warn("Пользователь ввёл не число");
                scanner.nextLine();
                System.out.print("Выберите действие: ");
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            logger.info("Выбрана команда: {}", choice);

            switch (choice) {

                case 1:
                    System.out.println("Создание пользователя");
                    break;

                case 2:
                    System.out.println("Поиск пользователя по ID");
                    break;

                case 3:
                    System.out.println("Список пользователей");
                    break;

                case 4:
                    System.out.println("Обновление пользователя");
                    break;

                case 5:
                    System.out.println("Удаление пользователя");
                    break;

                case 0:
                    running = false;
                    System.out.println("Программа завершена.");
                    break;

                default:
                    System.out.println("Неизвестная команда.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("USER SERVICE");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }
}

