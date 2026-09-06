package com.example;

import com.example.dao.UserDao;
import com.example.dao.UserDaoImpl;
import com.example.entity.User;
import com.example.util.HibernateUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
/**
 * Точка входа в приложение.
 * Реализует консольное меню для CRUD-операций над пользователями.
 * Вся работа с базой данных выполняется через UserDao —
 * класс не содержит логики работы с Hibernate напрямую.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDaoImpl();
    /**
     * Запускает консольное меню. Работает, пока пользователь не выберет пункт "0".
     * Перед завершением закрывает SessionFactory.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        log.info("Приложение запущено");
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Выберите действие: ");

            switch (choice) {
                case 1 -> createUser();
                case 2 -> findUser();
                case 3 -> showAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    running = false;
                    System.out.println("Программа завершена.");
                }
                default -> System.out.println("Неизвестная команда, попробуйте снова.");
            }
        }

        HibernateUtil.shutdown();
        log.info("Приложение остановлено");
    }
    /** Печатает главное меню в консоль. */
    private static void printMenu() {
        System.out.println();
        System.out.println("=== USER SERVICE ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
    }
    /** Запрашивает данные и создаёт нового пользователя. */
    private static void createUser() {
        String name = readString("Имя: ");
        String email = readString("Email: ");
        int age = readInt("Возраст: ");

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Создан пользователь: " + user);
    }
    /** Ищет пользователя по id и печатает результат. */
    private static void findUser() {
        long id = readLong("Введите ID: ");
        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            System.out.println("Найден: " + user.get());
        } else {
            System.out.println("Пользователь с id=" + id + " не найден.");
        }
    }
    /** Печатает список всех пользователей. */
    private static void showAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователей пока нет.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }
    /** Обновляет поля существующего пользователя. Пустой ввод оставляет старое значение. */
    private static void updateUser() {
        long id = readLong("Введите ID пользователя: ");
        Optional<User> found = userDao.findById(id);

        if (found.isEmpty()) {
            System.out.println("Пользователь с id=" + id + " не найден.");
            return;
        }

        User user = found.get();
        System.out.println("Текущие данные: " + user);

        String name = readString("Новое имя (Enter — оставить без изменений): ");
        if (!name.isBlank()) {
            user.setName(name);
        }

        String email = readString("Новый email (Enter — оставить без изменений): ");
        if (!email.isBlank()) {
            user.setEmail(email);
        }

        String ageText = readString("Новый возраст (Enter — оставить без изменений): ");
        if (!ageText.isBlank()) {
            try {
                user.setAge(Integer.parseInt(ageText.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Возраст не распознан, оставляю старый.");
            }
        }

        userDao.update(user);
        System.out.println("Обновлено: " + user);
    }
    /** Удаляет пользователя по id. */
    private static void deleteUser() {
        long id = readLong("Введите ID пользователя: ");
        userDao.deleteById(id);
        System.out.println("Готово.");
    }
    /**
     * Читает целое число из консоли.
     * Повторяет запрос, пока пользователь не введёт корректное число.
     *
     * @param prompt текст-подсказка перед вводом
     * @return введённое число
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число!");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число!");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}