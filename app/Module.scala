import com.google.inject.AbstractModule
import com.google.inject.name.Names
import persistence.{TodoRepository, TodoRepositoryOnmMemoryImpl}

class Module extends AbstractModule {
    override def configure() = {
        bind(classOf[TodoRepository]).to(classOf[TodoRepositoryOnmMemoryImpl])
    }
}