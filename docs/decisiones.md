# Decisión 1 · ¿Bloquear la orientación?

1. **Qué decidimos:** no bloquear la orientación; no ponemos `android:screenOrientation="portrait"` en el manifiesto.
2. **Por qué:** bloquearla solo evita el giro, pero idioma, tamaño de letra y la muerte del proceso también destruyen la pantalla (lo comprobamos en las secuencias del ciclo de vida), así que el problema seguiría.
3. **A qué renunciamos:** a la comodidad de ignorar el estado; hay que guardarlo y restaurarlo siempre, y diseñar también para horizontal.
4. **Qué pasa en React Native:** la decisión se traslada igual, el estado de la pantalla hay que conservarlo porque el sistema puede destruirla.
5. **Dónde va cada dato:** el texto a medio escribir en el formulario va al Bundle (se puede perder), un ajuste del usuario va a preferencias y las rutas e incidencias van a base de datos.


# Decisión 2 · ¿Una pantalla con scroll o dividida en pasos?

1. **Qué decidimos:** la pantalla de alta de ruta va en una sola pantalla con una zona de scroll, no dividida en pasos.
2. **Por qué:** es menos abrumador y el usuario lo ve todo en una vista; con 2 campos en mi producto y 6 en Sendeiro no hace falta dividirla.
3. **A qué renunciamos:** a guiar al usuario paso a paso y a guardar cada paso por separado; lo compensamos dejando el título y el botón Guardar siempre visibles.
4. **Qué pasa en React Native:** se traslada igual, con una zona de scroll entre una cabecera y un botón fijos.
5. **Cuándo cambiaría:** si la pantalla llegara a muchos campos (como los 11 del parte de incidencia), donde convendría dividir en pasos con borrador.

# Decisión 3 · ¿Botón de guardar desactivado o siempre activo?

1. **Qué decidimos:** el botón Guardar está desactivado hasta que todos los datos obligatorios sean válidos.
2. **Por qué:** con pocos campos se entiende qué falta, y así no se puede guardar un dato malo.
3. **A qué renunciamos:** a explicar qué falta, porque un botón gris no dice qué campo falla; lo compensamos marcando el error de cada campo al salir de él.
4. **Qué pasa en React Native:** se traslada igual, con el botón desactivado según el estado de los campos.
5. **Cuándo cambiaría:** si hubiera muchos campos obligatorios, donde sería mejor dejar el botón activo y avisar al pulsar.


# Decisión 4 · ¿Cómo se borra una ruta del listado?

1. **Qué decidimos:** una papelera en cada fila, con una confirmación («¿Borrar esta ruta?») antes de borrar.
2. **Por qué:** coste de implementación bajo (un botón y un diálogo), accesible (tiene `contentDescription` y un área de pulsación de 48 dp) y el borrado accidental queda cubierto por la confirmación.
3. **A qué renunciamos:** a la rapidez de deslizar, y cada borrado cuesta un toque más (la confirmación); deslizar parece más moderno pero obliga a programar «deshacer», que es mucho más trabajo.
4. **Qué pasa en React Native:** se traslada igual, con un botón por fila en la lista y una alerta de confirmación antes de quitar el elemento.
5. **Cuándo cambiaría:** si el usuario tuviera que borrar muchas rutas de golpe, donde sería mejor una selección múltiple.