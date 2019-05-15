package usecasegen;

import com.change_vision.jude.api.inf.model.IClass;

public class Tuple {

	private IClass master;
	private IClass depended;

	public Tuple(IClass mtp, IClass dtp){
		master = mtp;
		depended = dtp;
	}

	public IClass getMaster(){
		return master;
	}

	public IClass getDepended(){
		return depended;
	}

	public void setMaster(IClass tp1){
		master = tp1;
		return;
	}

	public void setDepended(IClass tp2){
		depended = tp2;
		return;
	}

}