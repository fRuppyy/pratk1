Реализовать следующую многопоточную систему.
Файл. Имеет следующие характеристики:
0. Тип файла (например XML, JSON, XLS)
1. Размер файла — целочисленное значение от 10 до 100.
Генератор файлов -- генерирует файлы с задержкой от 100 до 1000 мс.
Очередь — получает файлы из генератора. Вместимость очереди — 5
файлов.
Обработчик файлов — получает файл из очереди. Каждый обработчик
имеет параметр — тип файла, который он может обработать. Время обработки
файла: «Размер файла*7мс»
Система должна удовлетворять следующими условиям:
0. Должна быть обеспечена потокобезопасность.
1. Работа генератора не должна зависеть от работы обработчиков, и
наоборот.
2. Если нет задач, то потоки не должны быть активны.
3. Если нет задач, то потоки не должны блокировать другие потоки.
4. Должна быть сохранена целостность данных.
