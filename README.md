# WEB BENTAYA - BACKEND

El desarrollo se ha hecho usando IntelliJ Ultimate, se recomienda su uso.

## Instalación de la base de datos y azurite

1. Únicamente hay que ejecutar el archivo `web-bentaya-backend/docker-compose.yml` (es necesario tener Docker instalado)
   - Se crearán dos instancias, una para la base de datos (mysql) y otra para guardar los archivos como pdfs o
     imágenes (azurite). Las configuraciones por defecto ya están en las application-local.properties

## Instalación de librerías

1. Cargar las librerías de Maven (el IntelliJ lo hace solo o dándole a las flechas circulares en la pestaña de Maven)
2. Instalar la librería de Redsys ejecutando el comando:
   `mvn install:install-file -Dfile=${project.basedir}/libs/apiSha256.jar -DgroupId=sis.redsys.api -DartifactId=api-sha256 -Dversion=2.3 -Dpackaging=jar`

## Configuración para el desarrollo

### Variables de entorno

Para ejecutar WebBentayaBackendApplication será necesario añadir las siguientes variables de entorno:

* WEB_EMAIL_USERNAME (dirección de correo que envía los correos)
* WEB_EMAIL_PASSWORD (contraseña de aplicación de dicho correo)

Si deseas tener correo funcional añadir dirección de correo electrónico con su respectiva contraseña de aplicación. Si
no deseas añadir un correo, pon cualquier valor a la variable de entorno. **NO** añadir correos desconocidos que no
estén bajo tu control.

Si deseas tener un entorno de pago funcional, instalar ngrok, ejecutarlo y añadir la variable de entorno TPV_URL con la
dirección proporcionada por ngrok.

Puedes copiar las variables de entorno en el formato de IntelliJ a continuación. Con estas variables de entorno
funcionará la aplicación, pero puedes cambiar los valores según lo comentado anteriormente:

```
WEB_EMAIL_PASSWORD=xxxxxxxxxxxxxxxx;WEB_EMAIL_USERNAME=fakemail@fakebentaya.org;TPV_URL=https://la-url-se-ve-algo-asi.ngrok-free.app;
```

### Perfil

También tendrás que especificar en 'Active profiles' local-mysql.

## Despliegue

1. Al actualizar la rama 'main' se creará un workflow en GitHub que actualizará automáticamente la app. Si se actualiza
   la rama test, se actualizará la app del entorno de test.
2. El entorno de test y/o producción deberá contar con las siguientes variables de entorno:
    * WEB_EMAIL_USERNAME (dirección de correo que envía los correos)
    * WEB_EMAIL_PASSWORD (contraseña de aplicación de dicho correo)
    * WEB_DB_URL (url a la base de datos)
    * WEB_DB_USERNAME (usuario de la base de datos)
    * WEB_DB_PASSWORD (contraseña del usuario)
    * TPV_ID (número del TPV virtual)
    * TPV_PASSWORD (contraseña del TPV)
    * JWT_SECRET (string del JWT)
    * JWT_CALENDAR_SECRET (string del JWT del calendario)
    * BENTAYA_WEB_URL (url de la web)
   * AZURE_BLOB_CONNECTION_STRING (connection string de la cuenta de Azure Connections para guardar archivos)
