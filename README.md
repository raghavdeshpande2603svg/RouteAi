# Accessible-Campus-Navigation
Java project for finding accessible routes around campus. (UI design)

Run the application from this folder:

```text
javac Main.java Authentication.java CampusMap.java AccessibleCampusNavigation.java
java Main
```

Sign in with ID `admin` and password `admin` to open the campus map.

The map uses A* search to find minimum-cost routes. Wheelchair mode excludes
stairs and inaccessible connections; mobility mode keeps the existing
preference for accessible routes by assigning a penalty to those connections.
