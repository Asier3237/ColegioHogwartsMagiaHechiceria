# ColegioHogwartsMagiaHechiceria
🧙‍♀️ Hogwarts Web App - Sprint 1
Bienvenido al repositorio del Sprint 1 del proyecto Hogwarts Web App. En esta primera fase nos centramos en construir los cimientos del sistema mágico: la base de datos, el sistema de autenticación y el sombrero seleccionador.

🗃️ Base de Datos
La base de datos está diseñada para reflejar la estructura del colegio Hogwarts y sus funcionalidades principales. Las tablas implementadas incluyen:

Tabla	Descripción
usuarios	Almacena nombre, email, contraseña cifrada, rol, experiencia, nivel y casa asignada.
casas	Gryffindor, Hufflepuff, Ravenclaw, Slytherin. Se guardan nombre y puntos.
roles	Define si el usuario es alumno, profesor o administrador.
preferencias_casa	Guarda el orden de preferencia de casas indicado por el usuario al registrarse.
🔐 Las contraseñas se almacenan cifradas. La estructura está normalizada y preparada para futuras ampliaciones como hechizos, pociones e ingredientes.

🔐 Login y Registro
El sistema de autenticación incluye:

Registro de nuevos usuarios con validación de datos.

Login con comprobación de credenciales.

Asignación de rol y casa tras el registro.

Control de acceso según el rol del usuario.

🧩 El sistema está preparado para integrar múltiples roles por usuario en futuros sprints.

🎩 Sombrero Seleccionador
Una de las funcionalidades más mágicas del sistema ✨

Funcionamiento:
Al registrarse, el usuario indica su orden de preferencia de casas (del 1 al 4).

El sistema valida que no haya duplicados ni valores fuera de rango.

El sombrero seleccionador analiza las preferencias y asigna una casa teniendo en cuenta:

Disponibilidad de plazas (si se aplica).

Preferencias del usuario.

Algoritmo interno de asignación (no aleatorio puro).

📌 Si el usuario no indica preferencias, se asigna una casa aleatoriamente.

📅 Estado del Sprint
✅ Base de datos en proceso.

✅ Login y registro en proceso.

✅ Sombrero seleccionador en proceso.

🔜 Próximo sprint: interfaz gráfica, lógica de hechizos y pociones, y conexión con servidor.
