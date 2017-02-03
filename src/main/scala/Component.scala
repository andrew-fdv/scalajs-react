package eldis.react

import scalajs.js
import js.annotation._

@JSImport("react", "Component")
@js.native
abstract class RawComponent extends js.Any {

  type State

  @JSName("props")
  def propsNative: js.Any = js.native

  @JSName("state")
  protected def stateRaw: Wrapped[State] = js.native

  @JSName("setState")
  def setStateRaw(s: Wrapped[State]): Unit = js.native

  def initialState: State

  def render(): ReactNode

  protected def componentWillUpdate(nextProps: js.Any, nextState: Wrapped[State]): Unit = js.native
  protected def componentDidUpdate(prevProps: js.Any, prevState: Wrapped[State]): Unit = js.native
  protected def componentDidMount(): Unit = js.native
  protected def componentWillUnmount(): Unit = js.native
}

@ScalaJSDefined
abstract class ComponentBase[F[_]: UnwrapNative, P: WrapToNative] extends RawComponent {

  type Props = P

  var stateInitialized = false

  @JSName("propsImpl")
  def props: Props = implicitly[UnwrapNative[F]].unwrap(propsNative)

  def propsChildren: js.Array[ReactNode] = {
    //TODO: cache it and invalidate when receive new props
    val ch = Option(propsNative.asInstanceOf[js.Dynamic])
      .flatMap(p => Option(p.children.asInstanceOf[js.Any]))
      .map(ch => (if (ch.isInstanceOf[js.Array[_]]) ch.asInstanceOf[js.Array[ReactNode]] else js.Array(ch.asInstanceOf[ReactNode])))
    ch.getOrElse(js.Array[ReactNode]())
  }

  def this(name: String) {
    this()
    this.asInstanceOf[js.Dynamic].constructor.displayName = name
  }

  @JSName("createElement")
  def apply(p: Props, children: ReactNode*): ReactDOMElement = {
    val c = this.asInstanceOf[js.Dynamic].constructor
    JSReact.createElement(c, implicitly[WrapToNative[P]].wrap(p), children: _*)
  }

  @JSName("createElementNoProps")
  def apply(children: ReactNode*): ReactDOMElement = {
    val c = this.asInstanceOf[js.Dynamic].constructor
    JSReact.createElement(c, (), children: _*)
  }

  @JSName("stateImpl")
  def state: State = {
    if (stateInitialized == false) {
      this.asInstanceOf[js.Dynamic].state = Wrapped(initialState.asInstanceOf[js.Any])
      stateInitialized = true
    }
    stateRaw.get
  }

  @JSName("setStateImpl")
  def setState(s: State): Unit = {
    setStateRaw(Wrapped(s))
  }

  @JSName("componentWillUpdate")
  override protected def componentWillUpdate(nextProps: js.Any, nextState: Wrapped[State]): Unit = {
    willUpdate(implicitly[UnwrapNative[F]].unwrap(nextProps), Option(nextState).map(_.get))
  }

  @JSName("componentDidUpdate")
  override protected def componentDidUpdate(prevProps: js.Any, prevState: Wrapped[State]): Unit = {
    didUpdate(implicitly[UnwrapNative[F]].unwrap(prevProps), Option(prevState).map(_.get))
  }

  @JSName("componentDidMount")
  override protected def componentDidMount(): Unit = {
    didMount()
  }

  @JSName("componentWillUnmount")
  override protected def componentWillUnmount(): Unit = {
    willUnmount()
  }

  def willUpdate(nextProps: Props, nextState: Option[State]): Unit = {}
  def didUpdate(prevProps: Props, prevState: Option[State]): Unit = {}
  def didMount(): Unit = {}
  def willUnmount(): Unit = {}
}
