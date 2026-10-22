# Decisión 1 · ¿Bloquear la orientación?

1. **Qué decidimos:** no bloquear la orientación; no ponemos `android:screenOrientation="portrait"` en el manifiesto.
2. **Por qué:** bloquearla solo evita el giro, pero idioma, tamaño de letra y la muerte del proceso también destruyen la pantalla (lo comprobamos en las secuencias del ciclo de vida), así que el problema seguiría.
3. **A qué renunciamos:** a la comodidad de ignorar el estado; hay que guardarlo y restaurarlo siempre, y diseñar también para horizontal.
4. **Qué pasa en React Native:** la decisión se traslada igual, el estado de la pantalla hay que conservarlo porque el sistema puede destruirla.
5. **Dónde va cada dato:** el texto a medio escribir en el formulario va al Bundle (se puede perder), un ajuste del usuario va a preferencias y las rutas e incidencias van a base de datos.