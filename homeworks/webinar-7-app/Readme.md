## Отчет о выполнении домашнего задания №7

### Описание каждого этапа выполнения задания.

1.  **Реализация Deadlock:**
    *   Создан класс `DeadlockExample`, в котором два потока пытаются захватить два ресурса в противоположном порядке, что приводит к взаимоблокировке.

2.  **Использование jstack для диагностики Deadlock:**
    *   Для анализа deadlock необходимо запустить приложение, выбрать пункт 1, и после того, как программа зависнет, в терминале выполнить команду `jps` для получения PID процесса.
    *   Затем выполнить `jstack <pid>` для получения дампа потоков.
    *   В дампе потоков будет обнаружен deadlock, с указанием потоков и ресурсов, которые они пытаются захватить.

3.  **Моделирование LiveLock и Starvation:**
    *   **Livelock:** Создан класс `LivelockExample`, в котором два потока пытаются захватить два ресурса, но при неудаче освобождают уже захваченный ресурс и повторяют попытку. Это приводит к ситуации, когда потоки постоянно активны, но не могут выполнить свою работу.
    *   **Starvation:** Создан класс `StarvationExample`, в котором один поток с низким приоритетом не может получить доступ к общему ресурсу, так как его постоянно перехватывает поток с высоким приоритетом.

4.  **Предотвращение Deadlock, Livelock и Starvation:**
    *   **Deadlock:** Для предотвращения deadlock можно использовать упорядоченный захват ресурсов. Если все потоки будут захватывать ресурсы в одном и том же порядке, deadlock не возникнет.
    *   **Livelock:** Для предотвращения livelock можно использовать случайные задержки перед повторной попыткой захвата ресурса.
    *   **Starvation:** Для предотвращения starvation можно использовать "честные" блокировки (fair locks) из `java.util.concurrent.locks.ReentrantLock`, которые предоставляют доступ к ресурсу в порядке очереди.

### Дамп потоков с использованием jstack и объяснение Deadlock.

```
Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x00007f5ad0160020 (object 0x00000005f278bdc0, a java.lang.Object),
  which is held by "Thread-2"

"Thread-2":
  waiting to lock monitor 0x00007f5ad4015520 (object 0x00000005f278bdb0, a java.lang.Object),
  which is held by "Thread-1"

Java stack information for the threads listed above:
===================================================
"Thread-1":
	at ru.savka.webinar7app.service.DeadlockExample.lambda$execute$0(DeadlockExample.java:21)
	- waiting to lock <0x00000005f278bdc0> (a java.lang.Object)
	- locked <0x00000005f278bdb0> (a java.lang.Object)
	at ru.savka.webinar7app.service.DeadlockExample$$Lambda$550/0x0000000800e073e0.run(Unknown Source)
	at java.lang.Thread.run(java.base@17.0.6/Thread.java:833)
"Thread-2":
	at ru.savka.webinar7app.service.DeadlockExample.lambda$execute$1(DeadlockExample.java:38)
	- waiting to lock <0x00000005f278bdb0> (a java.lang.Object)
	- locked <0x00000005f278bdc0> (a java.lang.Object)
	at ru.savka.webinar7app.service.DeadlockExample$$Lambda$551/0x0000000800e07608.run(Unknown Source)
	at java.lang.Thread.run(java.base@17.0.6/Thread.java:833)

Found 1 deadlock.

```

**Объяснение:**

В выводе `jstack` видно, что два потока находятся в состоянии взаимной блокировки. Один поток ожидает ресурс, который захвачен другим потоком, и наоборот. Это и есть состояние взаимоблокировки (deadlock).

### Итоговые выводы о предотвращении Deadlock, LiveLock и Starvation в многозадачных приложениях.

Deadlock, Livelock и Starvation - это серьезные проблемы, которые могут возникнуть в многопоточных приложениях. Для их предотвращения необходимо тщательно проектировать архитектуру приложения и использовать правильные механизмы синхронизации. Упорядоченный захват ресурсов, использование "честных" блокировок и введение случайных задержек - это эффективные способы борьбы с этими проблемами.
