java -Xmx1024M -Xss2M -XX:MaxPermSize=512m -XX:+CMSClassUnloadingEnabled -Dfileencoding=UTF-8 -jar `dirname $0`/sbt-launch-0.12.1.jar "$@"
