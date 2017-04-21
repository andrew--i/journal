# journal

Реализация журнала для хранения событий и последующего их чтений.

Текущее ограничения:
+ Запись журнала (записывание) идет в локальный файл ( через прослойку Apache VFS )
+ Журнал хранится в отдельной папке
+ Запись журнала представляет собой json с произвольным набором полей ( jackson, logstash-logback-encoder )


Что реализовано:
+ Конфигурирование журналов через [joran configuration framework](https://logback.qos.ch/manual/onJoran.html)
+ Конфигурации журналов в отдельном файле
+ Механизм записи логов ( over slf4j-api )
+ Механизм чтения логов из "источника" (over apache commons virtual file system)  (одного, на основе [otroslogviewer](https://github.com/otros-systems/otroslogviewer))
+ Тест полного цикла работы с журналом ( открытие, запись, чтение ) ( JournalTest.java )
+ Чтение топ-записей из журнала
+ Конфигурация коллектора записей журнала ( блок reader в конфигурации журнала )
+ Удобный пользовательский api для записи в журнал ( JournalFactoryReadWriteTest.java )
+ Конфигурация журналов в runtime ( JournalFactory содержит экземпляры Journal, можно их редактировать )
+ Журнал соответствует папке ( или файлы журнала хранятся в папке )
+ Механизм синхронизации записей журнала ( когда пишут в один журнал разные инстансы ) ( реализовано через FileMonitor, который следит за файлами *.journal )
+ Учитывать мультиаредность (параметр в runtime для журнала, который определяет путь до папки с журналом. Запись и чтение )
+ Механизм ротации (время, размер) файлов журнала ( должно быть средствами logback, нужно написать тесты ) (JournalRotationTest.class)

В работе:
+ Файлы журнала хранятся в hdfs ( должно быть уже реализовано через apache common virtual file system, нужно написать тесты )

Фичи в рамках следующих итераций:
+ Удобный пользовательский api для изменения конфигурации журналирования в runtime ( config file scan, edit journal factory context )
+ настройку источинка логов для чтения брать из конфигурации аппендера ( или, наоборот, логгер настраивать из конфигурации журнала)
+ Реализовать logback аппендер в hdfs ( есть наработки для log4j )
+ Возможность просматривать историю журнала
+ Возможность фильтрации записей журнала
+ При длительном неиспользовании журнала арендатора, освобождать ресурсы связанные с журналами и данным арендатором

