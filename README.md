# journal

Реализация журнала для хранения событий и последующего их чтений.

Текущее ограничения:
+ Запись журнала (записывание) идет в один локальный файл
+ Журнал хранится в отдельной папке
+ Запись журнала представляет собой json с произвольным набором полей ( jackson )


Что реализовано:
+ Конфигурирование журналов через [joran configuration framework](https://logback.qos.ch/manual/onJoran.html)
+ Конфигурации журналов в отдельном файле
+ Механизм записи логов ( over slf4j-api )
+ Механизм чтения логов из "источника" (over apache commons virtual file system)  (одного, на основе [otroslogviewer](https://github.com/otros-systems/otroslogviewer))
+ Тест полного цикла работы с журналом ( открытие, запись, чтение ) ( JournalTest.java )
+ Чтение топ-записей из журнала
+ Конфигурация коллектора записей журнала ( блок reader в конфигурации журнала )

В работе:
+ Удобный пользовательский api для записи в журнал


Фичи в рамках следующих итераций:
+ Конфигурация журналов в runtime
+ Механизм ротации (время, размер) файлов журнала
+ Механизм синхронизации записей журнала ( когда пишут в один журнал разные инстансы )
+ Файлы журнала хранятся в hdfs
+ Удобный пользовательский api для изменения конфигурации журналирования в runtime

