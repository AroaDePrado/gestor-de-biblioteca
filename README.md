# Gestor de Biblioteca
Este es un proyecto de Java que representa una aplicación de biblioteca con una interfaz de usuario. Se puede utilizar para gestionar libros y préstamos. La aplicación te permite agregar, eliminar, editar y buscar libros, así como registrar préstamos de libros a usuarios. También proporciona funcionalidades para marcar los préstamos como devueltos.

## Funcionalidades

### Modos de Guardado
Puedes habilitar el autoguardado para que los datos se guarden automáticamente después de cada modificación, o desactivarlo y utilizar los botones "Guardar" y "Cargar" para gestionar manualmente los archivos de datos.

### Añadir Libro
Puedes agregar un libro proporcionando su título, autor y ISBN. Luego, puedes guardar la información en archivos de texto o habilitar el autoguardado para que los cambios se guarden automáticamente. El botón aparece deshabilitado cuando los campos de texto están vacíos para evitar errores.

### Eliminar Libro
Puedes seleccionar un libro de la tabla y eliminarlo. El botón aparece deshabilitado cuando no hay un libro seleccionado para evitar errores.

### Editar Libro
Permite modificar la información de un libro seleccionado. El botón aparece deshabilitado cuando no hay un libro seleccionado o los campos de texto están vacíos para evitar errores.

### Buscar Libro
Puedes buscar libros por título, autor o ISBN y filtrar los resultados en la tabla. El filtro se resetea al modificar, eliminar o registrar un préstamo con el libro buscado.

### Registrar Préstamo
Puedes registrar préstamos de libros a usuarios. Debes proporcionar el nombre de usuario, la fecha de inicio y la fecha de finalización del préstamo. Los selectores de fecha muestran por defecto la fecha actual y la de dentro de 15 días respectivamente con el objetivo de agilizar el registro de préstamos más comunes. Este valor de 15 días está definido por la constante `DURACION_ESTANDAR_PRESTAMO` y se puede modificar fácilmente si fuera necesario.

### Registrar Devoluciones
Cuando un libro es devuelto, puedes marcar el préstamo como "Devuelto" manualmente. Los préstamos cuya fecha de fin de plazo haya pasado son marcados automáticamente como "RETRASADO".
