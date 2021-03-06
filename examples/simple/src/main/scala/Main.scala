package eldis.react.examples.simple

import scalajs.js
import js.annotation.JSImport
import eldis.react._
import vdom.prefix_<^._
import org.scalajs.dom

object Main extends js.JSApp {

  def render(): ReactNode = {
    implicit val sShow = new WithTypeclass.Show[String] {
      override def show(s: String) = "*** " + s
    }
    <.div()(
      <.h4()("Create stateless elements:"),
      Stateless.nativeComponent(Stateless.NativeProps("Hello from native stateless component", key = "1")),
      Stateless.nativeComponentWithChildren(
        Stateless.NativeProps("Hello from native stateless component with children", key = "2"),
        <.p()("*** Hi, I'm child")
      ),
      Stateless.scalaComponent.withKey("3")(Stateless.ScalaProps("Hello from scala stateless component")),
      Stateless.scalaComponentWithChildren.withKey("4")(
        Stateless.ScalaProps("Hello from scala stateless component with children"),
        <.p()("*** Hi, I'm child")
      ),
      <.h4()("And now create stateful elements:"),
      Stateful.NativeComponent(
        Stateful.NativeProps("Hello from native stateful component", key = "5"),
        <.p()("*** Hi, I'm the child of native statfull component")
      ),
      Stateful.ScalaComponent.withKey("6")(
        Stateful.ScalaProps("Hello from scala stateful component with properties"),
        <.p()("*** Hi, I'm the child of scala statfull component")
      ),
      <.h4()("And now create JS element:"),
      JS("Hello from JS!"),
      <.h4()("Here is the example of the components using typeclasses:"),
      WithTypeclass.StatefulComponent(List("Stateful Element 1", "Stateful Element 2", "Stateful Element 3")),
      WithTypeclass.StatelessComponent(List("Stateless Element 1", "Stateless Element 2", "Stateless Element 3"))
    )
  }

  def main(): Unit = {

    ReactDOM.render(
      render(),
      dom.document.getElementById("root")
    )
  }

}
