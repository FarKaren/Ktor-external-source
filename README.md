# Ktor Annotation Service

   ## Описание

   Сервис, написанный на Kotlin с использованием Ktor, предоставляет аннотацию для переданного генетического варианта. 

   ## Сборка и развертывание сервиса

   ### Требования

   - Docker

   ### Сборка

   1. Склонируйте репозиторий:
      ```sh
      https://github.com/FarKaren/Ktor-external-source.git
      ```

   2. В файле docker-compose.yaml в volumes вместо "path/to" укажите путь до файла с базой данных с аннотациями, предполагается что файл у вас есть.<br>
      ```
         volumes:
          - path/to/database.aka:/database/database.aka
      ```

   3. Поднимите сервис:
      ```sh
      docker-compose up -d
      ```

   ### Доступ

   Сервис будет доступен по адресу `http://localhost:9900`.<br>
   Пример запроса: `http://localhost:9900/info?rac=NC_012920.1&lap=16158&rap=16160&refkey=A`

   ## Примечания

   - Убедитесь, что ваш файл database.aka находится в директории `./database`.
   - В случае возникновения ошибок проверьте логи контейнера:
      ```sh
      docker-compose logs -f
      ```
