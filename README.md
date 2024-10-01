# WEB BENTAYA - BACKEND

El desarrollo se ha hecho usando IntelliJ Ultimate, se recomienda su uso.

## Instalación de la base de datos

1. Únicamente hay que ejecutar el archivo `web-bentaya-backend/docker-compose.yml` (es necesario tener Docker instalado)

## Instalación de librerías

1. Cargar las librerías de Maven (el IntelliJ lo hace solo o dándole a las flechas circulares en la pestaña de Maven)
2. Instalar la librería de Redsys ejecutando el comando:
   `mvn install:install-file -Dfile=${project.basedir}/libs/apiSha256.jar -DgroupId=sis.redsys.api -DartifactId=api-sha256 -Dversion=2.3 -Dpackaging=jar`

## Configuración para el desarrollo

### Variables de entorno

Para ejecutar WebBentayaBackendApplication será necesario añadir las siguientes variables de entorno:

* WEB_EMAIL_USERNAME (dirección de correo que envía los correos)
* WEB_EMAIL_PASSWORD (contraseña de aplicación de dicho correo)
* BENTAYA_EMAIL_MAIN (dirección principal a la que se envían algunos correos)
* BENTAYA_EMAIL_IT (dirección de mantenimiento a la que se envían algunos correos)
* BENTAYA_EMAIL_BOOKING (dirección de reservas a la que se envían algunos correos)
* BENTAYA_EMAIL_TREASURY (dirección de tesorería a la que se envían algunos correos)
* Direcciones de unidades:
    * BENTAYA_EMAIL_GAR
    * BENTAYA_EMAIL_WAI
    * BENTAYA_EMAIL_BAO
    * BENTAYA_EMAIL_ART
    * BENTAYA_EMAIL_AUT
    * BENTAYA_EMAIL_ARI
    * BENTAYA_EMAIL_IDA

Si deseas tener correo funcional añadir dirección de correo electrónico con su respectiva contraseña de aplicación. Si
no deseas añadir un correo, pon cualquier valor a la variable de entorno. **NO** añadir correos desconocidos que no
estén bajo tu control.

Si deseas tener un entorno de pago funcional, instalar ngrok, ejecutarlo y añadir la variable de entorno TPV_URL con la
dirección proporcionada por ngrok.

Puedes copiar las variables de entorno en el formato de IntelliJ a continuación. Con estas variables de entorno funcionará la aplicación, pero puedes cambiar los valores según lo comentado anteriormente:
```
BENTAYA_EMAIL_ARI=fake_mail;BENTAYA_EMAIL_ART=fake_mail;BENTAYA_EMAIL_AUT=fake_mail;BENTAYA_EMAIL_BAO=fake_mail;BENTAYA_EMAIL_BOOKING=fake_mail;BENTAYA_EMAIL_GAR=fake_mail;BENTAYA_EMAIL_IDA=fake_mail;BENTAYA_EMAIL_IT=fake_mail;BENTAYA_EMAIL_MAIN=fake_mail;BENTAYA_EMAIL_TREASURY=fake_mail;BENTAYA_EMAIL_WAI=fake_mail;TPV_URL=;WEB_EMAIL_PASSWORD=fake_app_password;WEB_EMAIL_USERNAME=fake_mail
```

### Perfil

También tendrás que especificar en 'Active profiles' local-mysql.

## Despliegue

1. Al actualizar la rama 'main' se creará un workflow en GitHub que actualizará automáticamente la app.
2. El entorno de producción deberá contar con las siguientes variables de entorno, además de las mencionadas en el
   apartado de desarrollo.
    * WEB_DB_URL (url a la base de datos)
    * WEB_DB_USERNAME (usuario de la base de datos)
    * WEB_DB_PASSWORD (contraseña del usuario)
    * TPV_ID (número del TPV virtual)
    * TPV_PASSWORD (contraseña del TPV)
    * JWT_SECRET (string del JWT)
    * JWT_CALENDAR_SECRET (string del JWT del calendario)
